var currentPage = 1;
var dataPerPage = 6;
// alert(currentPage);
$(document).ready(function () {
    ajaxPage(currentPage);
});

function ajaxPage(currentPage){
    var apiUrl = "/main/search-all.get?currentPage={currentPage}&dataPerPage={dataPerPage}";
    apiUrl = apiUrl.replace("{currentPage}", currentPage);
    apiUrl = apiUrl.replace("{dataPerPage}", dataPerPage);
    // alert(apiUrl);
    $.ajax({
        url: apiUrl,
        type: "GET",
        success: function (data, status) {
            var jsonData = JSON.parse(data);
            console.log(jsonData);
            var dataBody = $("#tutor-card");
            dataBody.empty();
            for (var i = 0; i < jsonData.all_list.length; i++) {
                var person = jsonData.all_list[i];
                console.log(person);
                var link = "/main/tutors/";
                var imgSrc = "/assets/img/profile/";
                var imgId = "tutor-img";

                console.log(person.user_id);
                link = link + person.user_id;
                imgSrc = imgSrc + person.tutor_image;
                imgId = imgId + person.user_id;

                var div1 = $("<div>", {class: "col-12 col-md-6 mb-3"});
                var div2 = $("<div>", {class: "card border-light mb-4 animate-up-5"});
                var div3 = $("<div>", {class: "row no-gutters align-items-center"});
                var div4 = $("<div>", {class: "col-12 col-lg-6 col-xl-4"});
                var a1 = $("<a>", {href: link})
                var a2 = $("<a>", {href: link})
                var img = $("<img>", {
                    src: imgSrc,
                    alt: "loft space",
                    className: "card-img p-2 rounded-xl",
                    id: imgId
                });

                var div5 = $("<div>", {class: "col-12 col-lg-6 col-xl-7"});

                var div6 = $("<div>", {class: "card-body"});
                var div7 = $("<div>", {class: "mb-3"});

                var span1 = $("<span>", {
                        class: "h5 mb-3 mr-6",
                        text: person.name
                    }
                )

                var svg = $("<svg>", {
                    xmlns: "http://www.w3.org/2000/svg",
                    width: "200",
                    height: "200",
                    fill: "#dc143c",
                    class: "bi bi-heart-fill",
                    viewBox: "0 0 16 16",
                });


                var path = " <path fill-rule=\"evenodd\"\n" +
                    "d=\"M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314z\" />";

                var span2 = $("<span>", {
                        class: "h6 mb-3",
                        text: person.like
                    }
                )

                var div8 = $("<div>", {class: "mb-5"});

                var span3 = $("<span>", {class: "fas fa-map-marker-alt mr-2"});
                var span4 = $("<span>", {text: person.nationality});

                var div9 = $("<div>", {class: "d-flex my-3"})

                var span5 = $("<span>", {
                    class: "badge badge-pill badge-primary ml-2",
                    text: "#" + person.interest1
                })
                var span6 = $("<span>", {
                    class: "badge badge-pill badge-primary ml-2",
                    text: "#" + person.interest2
                })


                dataBody.append(div1);
                div1.append(div2);
                div2.append(div3);
                div3.append(div4);
                div4.append(a1);
                a1.append(img);
                div3.append(div5);
                div5.append(div6);
                div6.append(div7);
                div7.append(a2);
                a2.append(span1);
                a2.append('<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="#dc143c" class="bi bi-heart-fill mr-2" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314z"/></svg>');
                a2.append(span2);
                div6.append(div8);
                div8.append(span3);
                div8.append(span4);
                div6.append(div9);
                div9.append(span5);
                div9.append(span6);
            }
            paging(jsonData);
            $("#" + currentPage).addClass("active");
        }
    });
}

$("#search-form").submit(function (event) {
    event.preventDefault();


    var name = $('#search-name').val();
    var interest = $('#search-interest').val();
    var classDate = $('#reservate-date').val();

    console.log(name + " " + interest + " " + classDate);

    // var apiUrl2 = "/main/search-all/search?" + "name=" + name + "&interest=" + interest + "&classDate=" + classDate;
    var apiUrl2 = "/main/search-all/search?name={name}&interest={interest}&classDate={classDate}&currentPage={currentPage}&dataPerPage={dataPerPage}";
    apiUrl2 = apiUrl2.replace("{name}", name);
    apiUrl2 = apiUrl2.replace("{interest}", interest);
    apiUrl2 = apiUrl2.replace("{classDate}", classDate);

    // if(!name && !interest && !classDate) {
    //     alert("검색조건을 입력해주세요");
    //     return;
    // }
    // var apiUrl2 = "/main/search-all/search?";
    // // 검색 조건 입력 경우의 수
    // if(name){
    //     apiUrl2 += "name=" + name;
    // }
    // if(name && interest){
    //     apiUrl2 += "&interest=" + interest;
    // } else if(!name && interest) {
    //     apiUrl2 += "interest=" + interest;
    // }
    // if(!name && !interest){
    //     apiUrl2 += "classDate=" + classDate;
    // } else {
    //     apiUrl2 += "&classDate=" + classDate;
    // }

    // apiUrl2 += "&currentPage={currentPage}&dataPerPage={dataPerPage}";
    apiUrl2 = apiUrl2.replace("{currentPage}", currentPage);
    apiUrl2 = apiUrl2.replace("{dataPerPage}", dataPerPage);

    $.ajax({
        url: apiUrl2,
        type: "GET",
        success: function (data) {
            var jsonData = JSON.parse(data);
            console.log(jsonData);
            var dataBody = $("#tutor-card");

            dataBody.empty();
            for (var i = 0; i < jsonData.search_list.length; i++) {

                var person = jsonData.search_list[i];
                var link = "/main/tutors/";
                var imgSrc = "/assets/img/profile/";
                var imgId = "tutor-img";

                link = link + person.user_id;
                imgSrc = imgSrc + person.tutor_image;
                imgId = imgId + person.user_id;

                var div1 = $("<div>", {class: "col-12 col-md-6 mb-3"});
                var div2 = $("<div>", {class: "card border-light mb-4 animate-up-5"});
                var div3 = $("<div>", {class: "row no-gutters align-items-center"});
                var div4 = $("<div>", {class: "col-12 col-lg-6 col-xl-4"});
                var a1 = $("<a>", {href: link})
                var a2 = $("<a>", {href: link})
                var img = $("<img>", {
                    src: imgSrc,
                    alt: "loft space",
                    className: "card-img p-2 rounded-xl",
                    id: imgId
                });

                var div5 = $("<div>", {class: "col-12 col-lg-6 col-xl-7"});

                var div6 = $("<div>", {class: "card-body"});
                var div7 = $("<div>", {class: "mb-3"});

                var span1 = $("<span>", {
                        class: "h5 mb-3 mr-6",
                        text: person.name
                    }
                )

                var svg = $("<svg>", {
                    xmlns: "http://www.w3.org/2000/svg",
                    width: "200",
                    height: "200",
                    fill: "#dc143c",
                    class: "bi bi-heart-fill",
                    viewBox: "0 0 16 16",
                });

                var path = " <path fill-rule=\"evenodd\"\n" +
                    "d=\"M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314z\" />";

                var span2 = $("<span>", {
                        class: "h6 mb-3",
                        text: person.like
                    }
                )

                var div8 = $("<div>", {class: "mb-5"});

                var span3 = $("<span>", {class: "fas fa-map-marker-alt mr-2"});
                var span4 = $("<span>", {text: person.nationality});

                var div9 = $("<div>", {class: "d-flex my-3"})

                var span5 = $("<span>", {
                    class: "badge badge-pill badge-primary ml-2",
                    text: "#" + person.interest1
                })
                var span6 = $("<span>", {
                    class: "badge badge-pill badge-primary ml-2",
                    text: "#" + person.interest2
                })


                dataBody.append(div1);
                div1.append(div2);
                div2.append(div3);
                div3.append(div4);
                div4.append(a1);
                a1.append(img);
                div3.append(div5);
                div5.append(div6);
                div6.append(div7);
                div7.append(a2);
                a2.append(span1);
                a2.append('<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="#dc143c" class="bi bi-heart-fill mr-2" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314z"/></svg>');
                a2.append(span2);
                div6.append(div8);
                div8.append(span3);
                div8.append(span4);
                div6.append(div9);
                div9.append(span5);
                div9.append(span6);
            }
            paging(jsonData);
            $("#" + currentPage).addClass("active");

        },
        error: function (jqXHR, textStatus, errorThrown) {
            if (jqXHR.status === 400) {
                console.log("잘못된 요청입니다.");
            } else {
                console.log("서버에서 오류가 발생했습니다.");
            }
        }
    });
});

function paging(jsonData) {
    var paging = jsonData.paging[0];

    var totalPageCount = paging.totalPageCount;
    var startPage = paging.startPage;
    var endPage = paging.endPage;
    var prev = paging.prev;
    var next = paging.next;
    var pagePerOnce = paging.pagePerOnce;
    var size = paging.dataPerPage;
    var page = paging.currentPage;

    console.log("startPage: " + startPage);
    console.log("endPage: " + endPage);

    var ul = $("#pagination");
    ul.empty();

    // 이전 버튼
    var li1 = $("<li>", {class: "page-item", id: "previous-btn"});
    var a1 = $("<a>", {class: "page-link", id: "previous-btn-a", tabindex: "-1", href: ""});
    var text1 = "Previous";
    if (!prev) {
        li1.addClass("disabled");
    } else {
        a1.click(function (event) {
            currentPage = startPage - 1;
            event.preventDefault();
            ajaxPage(currentPage);
        })
    }
    ul.append(li1);
    li1.append(a1);
    a1.append(text1);

    // 페이지 버튼
    for (var i = startPage; i <= endPage; i++) {
        var li2 = $("<li>", {
            class: "page-item",
            id: i
        });
        var a2 = $("<a>", {
            class: "page-link",
            tabindex: "-1",
        });
        var text2 = i;

        ul.append(li2);
        li2.append(a2);
        a2.append(text2);
    }
    $(".page-link").click(function (event){
        event.preventDefault();
        currentPage = $(this).text();
        ajaxPage(currentPage);
    });

    // 다음 버튼
    var li3 = $("<li>", {class: "page-item"});
    var a3 = $("<a>", {class: "page-link", href: ""});
    var text3 = "Next";

    if (!next) {
       li3.addClass("disabled");
    } else {
        a3.click(function (event) {
            currentPage = endPage + 1;
            event.preventDefault();
            ajaxPage(currentPage);
        })
    }
    ul.append(li3);
    li3.append(a3);
    a3.append(text3);
}



