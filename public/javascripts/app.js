/**
 * Created by Matija on 10.11.2014..
 */

$(document).ready(function(ev){

    $("#generirajKljuceve").click(function(){
        $.ajax({
            url: '/asimetricniKljucevi/',
            type: "POST",
            dataType:"json",
            success: function (data,status) {
                console.log(status);
                var kljucevi=[];
                kljucevi=data.split("!END_OF_PRIVATE_KEY!")
                $("#privatniKljuc").val(kljucevi[0]);
                $("#javniKljuc").val(kljucevi[1]);
            },error:function(data,status,jqXHR){
                console.log(status);
            }
        });
    })

    $("#generirajTajniKljuc").click(function(){
        $.ajax({
            url: '/tajniKljuc',
            type: "POST",
            dataType:"json",
            success: function (data,status) {
                console.log(status);
                $("#tajniKljuc").val(data);
            },error:function(data,status,jqXHR){
                console.log(status);
            }
        });
    })

    $("#spremiPoruku").click(function(){
        $("#lblInfo").remove();
        var poruka=$("#porukaPosiljatelja").val();
        if(poruka.isEmpty)
        return;
        var data=JSON.stringify({"poruka":poruka});
        $.ajax({
            url: '/spremanjePoruke',
            type: "POST",
            contentType: 'application/json',
            dataType:"text",
            data:data,
            success: function (data,status) {
                console.log(status);
                $("#lblporuka").append("<span id='lblInfo' class='success label'>Poruka pohranjena</span>");
            },error:function(data,status,jqXHR){
                console.log(status);
            }
        });
    })

    $("#kriptirajPorukuAES").click(function(){
        $.ajax({
            url: '/kriptiranjePorukeAES',
            type: "POST",
            dataType:"json",
            success: function (data,status) {
                console.log(status);
                $("#kriptiranaPorukaAES").val(data);
            },error:function(data,status,jqXHR){
                console.log(status);
            }
        });
    })

    $("#dekriptirajPorukuAES").click(function(){
        $.ajax({
            url: '/dekriptiranjePorukeAES',
            type: "POST",
            dataType:"json",
            success: function (data,status) {
                console.log(status);
                $("#dekriptiranaPorukaAES").val(data);
            },error:function(data,status,jqXHR){
                console.log(status);
            }
        });
    })

    $("#kriptirajPorukuRSA").click(function(){
        $.ajax({
            url: '/kriptiranjePorukeRSA',
            type: "POST",
            dataType:"json",
            success: function (data,status) {
                console.log(status);
                $("#kriptiranaPorukaRSA").val(data);
            },error:function(data,status,jqXHR){
                console.log(status);
            }
        });
    })

    $("#dekriptirajPorukuRSA").click(function(){
        $.ajax({
            url: '/dekriptiranjePorukeRSA',
            type: "POST",
            dataType:"json",
            success: function (data,status) {
                console.log(status);
                $("#dekriptiranaPorukaRSA").val(data);
            },error:function(data,status,jqXHR){
                console.log(status);
            }
        });
    })


    $("#generirajSazetak").click(function(){
        $.ajax({
            url: '/sazetak',
            type: "POST",
            dataType:"json",
            success: function (data,status) {
                console.log(status);
                $("#sazetak").val(data);
            },error:function(data,status,jqXHR){
                console.log(status);
            }
        });
    })

    $("#digitalnoPotpisi").click(function(){
         $.ajax({
             url: '/digitalniPotpis',
             type: "POST",
             dataType:"json",
             success: function (data,status) {
                 console.log(status);
                 $("#digitalniPotpis").val(data);
             },error:function(data,status,jqXHR){
                 console.log(status);
         }
         });
     })


    $("#provjeraDigitalnogPotpisa").click(function(){
        $.ajax({
            url: '/provjeraDigitalnogPotpisa',
            type: "POST",
            dataType:"json",
            success: function (data,status) {
                console.log(status);
                if(data=="true")
                    $("#lbl").append("<div data-alert class='alert-box success'>Izračunati sažetak jednak je pristiglom sažetku!<a href='#' class='close'>&times;</a></div>");
                else
                    $("#lbl").append("<div data-alert class='alert-box alert'>Izračunati sažetak nije jednak pristiglom sažetku!<a href='#' class='close'>&times;</a></div>");

            },error:function(data,status,jqXHR){
                console.log(status);
            }
        });
    })



});