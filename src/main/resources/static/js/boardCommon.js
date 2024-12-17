$(document).ready(function() {
	
	
    const activeElement = $("#mainSection .board-qna, #mainSection .board-knowledge");
	
	  const navItem = $(`.nav-item.${activeElement}`);
	
	    $(".nav-item").removeClass("active");
        navItem.addClass("active");
	
	
});