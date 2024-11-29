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

function deleteUser(id) {
	swal({
		title: "Are you sure?",
		text: "You want to delete!",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
		.then((willDelete) => {
			if (willDelete) {
				window.location = "/user/deleteUser/" + id;
			} else {
				swal("You are safe!");
			}
		});
};

const search = () => {

	let query = $("#search-input").val();

	if (query == '') {
		$(".search-result").hide();

	} else {
		// searching logic
		// console.log(query);

		// sending request to server
		let url = `http://localhost:8080/search/${query}`;

		fetch(url).then((response) => {

			return response.json();

		}).then((data) => {
			// data
			// console.log(data);

			let text = `<div class='list-group'>`;

			data.forEach((contact) => {
				text += `<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'> ${contact.name} </a>`;
			});

			text += `</div>`;

			$(".search-result").html(text);
			$(".search-result").show();

		});
	}
}