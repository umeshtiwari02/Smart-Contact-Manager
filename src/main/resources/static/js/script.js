console.log("This is script file")

const toggleSidebar = () => {
	console.log("Clicked")
	if ($(".sidebar").is(":visible")) {
		// if true -- need to close

		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	} else {
		//if false -- need to show

		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
};