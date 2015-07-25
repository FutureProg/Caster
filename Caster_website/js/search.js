var query = document.URL.match(/q=(.*)/)[1];            
$(document).ready(function(){
    console.log(query);
    $.ajax({
        type:"GET",
        data:{"q":query},
        url:"/php/search_podcasts.php"
    }).done(function(result){        
        $("#search-page").html(result);
    });
});