import {getId, setupHeaderAjax} from './checkTokenExpiration.js';

$(document).ready(function () {

    // 변수 선언
    const token = sessionStorage.getItem('accessToken');
    let userId

    // nullPointerException 예방
    if(token != null) {
        userId = getId(token)
        setupHeaderAjax(token)
    }

    // 로그인 로그아웃 버튼 바꾸기
    if (token != null) {
        $('#sign-btn').text('SIGN OUT');
    }

    // 비회원 마이페이지 버튼 안보 이게 하기
    if(token == null){
        $('#mypage-btn').hide();
    }

    //myPage
    $("#mypage-btn").click(function (e) {
        e.preventDefault();

        $.ajax({
            type: "GET",
            url: "/mypage/" + userId,
            success: function (data, textStatus, xhr) {
                console.log("controller return html: " + data)
                location.href = data
            },
            error: function (xhr, textStatus, errorThrown) {
                console.error("Error fetching mypage:", errorThrown);
            }
        });
    })

    $('#sign-btn').click(function() {

        if (token != null) {
            sessionStorage.removeItem('accessToken');
            document.cookie = "refreshToken=;  expires=Thu, 01 Jan 1970 00:00:00 UTC ; path=/";

            $.ajax({
                url: '/members/signout',
                type: 'POST',
                success: function (data) {
                    console.log('Signed out successfully');
                    location.href = "/main"
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.error('Error signing out:', textStatus, errorThrown);
                }
            });
        }else{
            location.href = "/members/signin"
        }
    });

    //sign - main은 적용 X해야함 이해안되면 명진에게 물어보세영
    // $('#sign-btn').click(function() {
    //     const token = sessionStorage.getItem('token');
    //     if (token != null) {
    //         sessionStorage.removeItem('token');
    //         $.ajax({
    //             url: '/members/signout',
    //             type: 'POST',
    //             success: function (data) {
    //                 console.log('Signed out successfully');
    //                 location.href = "/main"
    //             },
    //             error: function (jqXHR, textStatus, errorThrown) {
    //                 console.error('Error signing out:', textStatus, errorThrown);
    //             }
    //         });
    //     }else{
    //         location.href = "/members/signin"
    //     }
    // });
    // $('#signout-btn').click(function() {
        // if ($("#signin-btn").text() == "SIGN IN") {
        //     location.href = "/members/signin"
        // }
        // else {
        //     location.href = "/members/signout"
        // }
    // })


})

