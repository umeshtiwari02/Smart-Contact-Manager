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

function deleteContact(cid) {
	swal({
		title: "Are you sure?",
		text: "You want to delete this contact!",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((willDelete) => {
			if (willDelete) {
				window.location = "/user/delete/" + cid;
			} else {
				swal("Your contact is safe!");
			}
		});
};
