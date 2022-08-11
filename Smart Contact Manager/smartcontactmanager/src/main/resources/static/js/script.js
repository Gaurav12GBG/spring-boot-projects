console.log("This is a script.js file")

// const currentLocation = location.href;
// console.log(currentLocation);

// const multipleItem = document.querySelectorAll('ul li a');
// console.log(multipleItem)

// const multipleItemLength = multipleItem.length;
// console.log(multipleItemLength)

// for(var i=0; i<multipleItemLength; i++){
//     if(multipleItem[i].href === currentLocation){
//         console.log("Yes i am in")
//         multipleItem[i].className = "active";
//     }
// }

const toggleSidebar = () => {

    if($(".sidebar").is(":visible")){
        // true
        //band karna hai
        $(".sidebar").css("display", "none")
        $(".barsBtn").css("background-color", "white")
        $(".content").css("margin-left", "0%")
    }else{
        //false
        //start karna hai
        $(".sidebar").css("display", "block")
        $(".barsBtn").css("background-color", "")
        $(".content").css("margin-left", "20%")
    }
}