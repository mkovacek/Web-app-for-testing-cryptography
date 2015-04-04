package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.mvc.*;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import views.html.*;

import javax.crypto.*;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.apache.commons.io.FileUtils.readFileToString;

public class Application extends Controller {

    private static final String putanjaTajniKljuc="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\tajni_kljuc.txt";
    private static final String putanjaPrivatniKljuc ="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\privatni_kljuc.txt";
    private static final String putanjaJavniKljuc="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\javni_kljuc.txt";
    private static final String putanjaPoruka ="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\poruka.txt";
    private static final String putanjaKriptiranaPorukaAes ="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\kriptirana_porukaAES.txt";
    private static final String putanjaKriptiranaPorukaAESByte ="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\kriptirana_porukaAESByte.txt";
    private static final String putanjaDekriptiranaPorukaAes ="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\dekriptirana_porukaAES.txt";
    private static final String putanjaKriptiranaPorukaRSA="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\kriptiranaPorukaRSA.txt";
    private static final String putanjaKriptiraniPorukaRSAByte="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\kriptiranaPorukaRSAByte.txt";
    private static final String putanjaDekriptiranaPorukaRSA ="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\dekriptiraniPorukaRSA.txt";
    private static final String putanjaSazetak ="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\sazetak.txt";
    private static final String putanjaProvjereniDigitalniPotpis ="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\provjereniDigitalniPotpis.txt";
    private static final String putanjaDigitalniPotpis ="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\digitalniPotpis.txt";
    private static final String putanjaDigitalniPotpisByte ="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\digitalniPotpisByte.txt";
    private static final String putanjaDekriptiraniDigitalniPotpis ="C:\\Users\\Matija\\Documents\\FOI-predmeti,materijali\\4.godina\\7.semestar\\OS2\\DigitalniPotpis\\TXTfiles\\dekriptiraniDigitalniPotpis.txt";

    private static SecretKey tajniKljucAES=new SecretKey() {
        @Override
        public String getAlgorithm() {
            return null;
        }

        @Override
        public String getFormat() {
            return null;
        }

        @Override
        public byte[] getEncoded() {
            return new byte[0];
        }
    };


    public static Result index() {
        return ok(index.render());
    }

    public static Result kreiranjeAsimetricnihKljucevaRSA(){
        try {

            KeyPairGenerator generatorKljuceva = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            generatorKljuceva.initialize(2048, random);

            KeyPair par = generatorKljuceva.generateKeyPair();
            PrivateKey privatniRSAkljuc = par.getPrivate();
            PublicKey javniRSAkljuc = par.getPublic();

            byte[] privatniBytes = privatniRSAkljuc.getEncoded();
            byte[] javniBytes = javniRSAkljuc.getEncoded();

            BASE64Encoder encoder = new BASE64Encoder();
            String privatniRSAstring = encoder.encode(privatniBytes);
            String javniRSAstring = encoder.encode(javniBytes);

            spremanjeDatoteke(putanjaPrivatniKljuc,privatniRSAstring);
            spremanjeDatoteke(putanjaJavniKljuc,javniRSAstring);

            Gson gson = new Gson();
            String kljucevi=gson.toJson(privatniRSAstring+"!END_OF_PRIVATE_KEY!"+javniRSAstring);

            return ok(kljucevi);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ok();

    }

    public static Result kreiranjeSimetricnogKljucaAES(){
        try {
            KeyGenerator generatorKljuca = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            generatorKljuca.init(128, random);
            tajniKljucAES=generatorKljuca.generateKey();

            byte[] tajniBytes = tajniKljucAES.getEncoded();
            BASE64Encoder encoder = new BASE64Encoder();
            String tajniKljucAesString = encoder.encode(tajniBytes);
            spremanjeDatoteke(putanjaTajniKljuc,tajniKljucAesString);

            Gson gson = new Gson();
            String kljuc=gson.toJson(tajniKljucAesString);

            return ok(kljuc);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ok("erorr");
    }

    public static Result spremanjePoruke(){

        JsonNode json = request().body().asJson();

        if (json == null)
            Logger.info("bad json request");
        try{
            String poruka = json.findValue("poruka").asText();
            spremanjeDatoteke(putanjaPoruka,poruka);
            return ok("success");
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ok("error");
    }

    public static Result kriptiranjePorukeAES(){

        String poruka=citanjeIzDatoteke(putanjaPoruka);

        Cipher cipher = null;
        try {

            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,tajniKljucAES);

            byte[] encrypted = cipher.doFinal(poruka.getBytes());

            BASE64Encoder encoder = new BASE64Encoder();
            String kriptiranaPoruka=encoder.encode(encrypted);

            spremanjeDatotekeByte(putanjaKriptiranaPorukaAESByte,encrypted);
            spremanjeDatoteke(putanjaKriptiranaPorukaAes,kriptiranaPoruka);

            Gson gson = new Gson();
            String kriptPoruka=gson.toJson(kriptiranaPoruka);

            return ok(kriptPoruka);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return ok("error");
    }

    public static Result dekriptiranjePorukeAES(){
        byte[] kriptiranaPoruka=citanjeIzDatotekeByte(putanjaKriptiranaPorukaAESByte);

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,tajniKljucAES);
            byte[] decrypted = cipher.doFinal(kriptiranaPoruka);
            String dekriptiranaPoruka=new String(decrypted);
            spremanjeDatoteke(putanjaDekriptiranaPorukaAes,dekriptiranaPoruka);

            Gson gson=new Gson();
            String dekriptPoruka=gson.toJson(dekriptiranaPoruka);

            return ok(dekriptPoruka);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return ok("error");
    }

    public static Result kriptiranjePorukeRSA(){
        String javniKljuc=citanjeIzDatoteke(putanjaJavniKljuc);
        String poruka=citanjeIzDatoteke(putanjaPoruka);

        BASE64Decoder decoder=new BASE64Decoder();
        try {

            byte[] javniBytes=decoder.decodeBuffer(javniKljuc);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey javni_recovered = kf.generatePublic(new X509EncodedKeySpec(javniBytes));

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,javni_recovered);
            byte[] encrypted = cipher.doFinal(poruka.getBytes());

            BASE64Encoder encoder = new BASE64Encoder();
            String kriptiranaPoruka=encoder.encode(encrypted);

            spremanjeDatoteke(putanjaKriptiranaPorukaRSA,kriptiranaPoruka);
            spremanjeDatotekeByte(putanjaKriptiraniPorukaRSAByte, encrypted);

            Gson gson=new Gson();
            String kriptPoruka=gson.toJson(kriptiranaPoruka);
            return ok(kriptPoruka);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return ok("error");
    }

    public static Result dekriptiranjePorukeRSA(){

        byte[] kriptiranaPoruka= citanjeIzDatotekeByte(putanjaKriptiraniPorukaRSAByte);
        String privatniKljuc=citanjeIzDatoteke(putanjaPrivatniKljuc);


        BASE64Decoder decoder=new BASE64Decoder();

        byte[] privatniBytes= new byte[0];
        try {

            privatniBytes = decoder.decodeBuffer(privatniKljuc);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privatniKljuc_recovered = kf.generatePrivate(new PKCS8EncodedKeySpec(privatniBytes));

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privatniKljuc_recovered);
            byte[] descrypted = cipher.doFinal(kriptiranaPoruka);

            String dekriptiranaPoruka=new String(descrypted);
            spremanjeDatoteke(putanjaDekriptiranaPorukaRSA,dekriptiranaPoruka);

            Gson gson=new Gson();
            String dekriptPoruka=gson.toJson(dekriptiranaPoruka);

            return ok(dekriptPoruka);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return ok("error");

    }


    public static Result sazetak(){

        String poruka=citanjeIzDatoteke(putanjaPoruka);
        String sazetakMd5=DigestUtils.md5Hex(poruka);
        spremanjeDatoteke(putanjaSazetak,sazetakMd5);

        Gson gson=new Gson();
        String sazetak=gson.toJson(sazetakMd5);

        return ok(sazetak);

    }

    public static Result digitalniPotpis(){
        String privatniKljuc=citanjeIzDatoteke(putanjaPrivatniKljuc);
        String sazetak=citanjeIzDatoteke(putanjaSazetak);

        BASE64Decoder decoder=new BASE64Decoder();
        try {

            byte[] privatniBytes=decoder.decodeBuffer(privatniKljuc);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privatniKljuc_recovered = kf.generatePrivate(new PKCS8EncodedKeySpec(privatniBytes));

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,privatniKljuc_recovered);
            byte[] encrypted = cipher.doFinal(sazetak.getBytes());

            BASE64Encoder encoder=new BASE64Encoder();
            String digitalniPotpis=encoder.encode(encrypted);

            spremanjeDatoteke(putanjaDigitalniPotpis,digitalniPotpis);
            spremanjeDatotekeByte(putanjaDigitalniPotpisByte, encrypted);

            Gson gson=new Gson();
            String digPotpis=gson.toJson(digitalniPotpis);

            return ok(digPotpis);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return ok("error");
    }

    public static Result provjeraDigitalnogPotpisa(){
       // String digPotpis=citanjeIzDatoteke(putanjaDigitalniPotpis);
        byte[] digitalniPotpis= citanjeIzDatotekeByte(putanjaDigitalniPotpisByte);
        String javniKljuc=citanjeIzDatoteke(putanjaJavniKljuc);
        String poruka=citanjeIzDatoteke(putanjaPoruka);

        try {
            BASE64Decoder decoder=new BASE64Decoder();
           // byte[] digitalniPotpis=decoder.decodeBuffer(digPotpis);
            byte[] javniBytes = decoder.decodeBuffer(javniKljuc);

            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey javni_recovered = kf.generatePublic(new X509EncodedKeySpec(javniBytes));

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, javni_recovered);
            byte[] descrypted = cipher.doFinal(digitalniPotpis);

            String dekriptiraniDigitalniPotpis=new String(descrypted);
            spremanjeDatoteke(putanjaDekriptiraniDigitalniPotpis,dekriptiraniDigitalniPotpis);

            String sazetakMd5 = DigestUtils.md5Hex(poruka);
            spremanjeDatoteke(putanjaProvjereniDigitalniPotpis,sazetakMd5);

            Gson gson=new Gson();
            String provjera="false";

            if(dekriptiraniDigitalniPotpis.equals(sazetakMd5)){
                 provjera=gson.toJson("true");
                 return ok(provjera);
            }else{
                 return ok(provjera);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return ok("error");
    }


    private static void spremanjeDatoteke(String putanja,String sadrzaj){

        File datoteka = new File(putanja);
        try {
            FileUtils.writeStringToFile(datoteka, sadrzaj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void spremanjeDatotekeByte(String putanja, byte[] sadrzaj){

        File datoteka = new File(putanja);
        try {
            FileUtils.writeByteArrayToFile(datoteka, sadrzaj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String citanjeIzDatoteke(String putanja){
        File datoteka=new File(putanja);
        try {
            return readFileToString(datoteka,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "erorr";
    }

    private static byte[] citanjeIzDatotekeByte(String putanja){
        File datoteka=new File(putanja);
        try {
            return readFileToByteArray(datoteka);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
