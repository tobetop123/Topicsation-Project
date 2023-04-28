package com.multicampus.topicsation.controller;

import com.multicampus.topicsation.dto.ClassDTO;
import com.multicampus.topicsation.dto.MyPageDTO;
import com.multicampus.topicsation.dto.MypageScheduleDTO;
import com.multicampus.topicsation.service.IMyPageService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/mypage")
public class MyPageController {

    @Autowired
    private IMyPageService service;

    @GetMapping("/admin")
    public String adminPage() {
        return "html/dashboard/myPage-admin";
    }

    @GetMapping("/{user_id}")
    public String myPage(@PathVariable("user_id") String userId) {
        String role = service.check_role(userId);
        if(role.equals("tutee")){
            return "html/dashboard/myPage-tutees_Information";
        }else if(role.equals("tutor")){
            return "html/dashboard/myPage-tutors_Information";
        }
        return "html/dashboard/myPage-admin";
    }


    @GetMapping("/{user_id}/schedule")
    public String schedulePage(@PathVariable("user_id") String userId) {
        String role = service.check_role(userId);
        if(role.equals("tutee")) {
            return "html/dashboard/myPage-tutees_Schedule";
        }else if(role.equals("tutor")){
            return "html/dashboard/myPage-tutors_Schedule";
        }
        return "html/dashboard/myPage-admin";
    }

    @GetMapping("/{user_id}/history")
    public String historyPage() {
        return "html/dashboard/myPage-tutees_CourseHistory";
    }


    @RestController
    @RequestMapping("/mypage")
    public class MyPageRestController {

        @GetMapping("/admin/get")
        public String adminPage() {
            List<MyPageDTO> list =service.view_admin();
            JSONArray jsonArray = new JSONArray();

            for (MyPageDTO dto : list){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId",dto.getUser_id());
                jsonObject.put("tutorName",dto.getName());
                jsonObject.put("approlDate",dto.getRegi_date());
                jsonObject.put("file",dto.getCertificate());

                jsonArray.add(jsonObject);
            }

            String jsonString = jsonArray.toString();

            return jsonString;
        }

        @PostMapping("/admin/success")
        public String adminSuccess(@RequestBody String userId){
            service.success(userId);
            return null;
        }

        @PostMapping("/admin/fail")
        public String adminFail(@RequestBody String userId){
            service.fail(userId);
            return null;
        }

        @GetMapping("/{user_id}/get")
        public String myPage(@PathVariable("user_id") String userId) {
            MyPageDTO myPageDTO;
            JSONObject jsonObject = new JSONObject();
            String role = service.check_role(userId);
            if (role.equals("tutor")) {
                myPageDTO = service.view_tutor(userId);

                jsonObject.put("profileImg", myPageDTO.getProfileimg());
                jsonObject.put("name", myPageDTO.getName());
                jsonObject.put("email", myPageDTO.getEmail());
                jsonObject.put("nationality", myPageDTO.getNationality());
                jsonObject.put("interest1", myPageDTO.getInterest1());
                jsonObject.put("interest2", myPageDTO.getInterest2());
                jsonObject.put("genderRadios", myPageDTO.getGender());
                jsonObject.put("password",myPageDTO.getPassword());

            } else if(role.equals("tutee")) {
                myPageDTO = service.view_tutee(userId);
                jsonObject.put("tutor-name", myPageDTO.getName());
                jsonObject.put("name", myPageDTO.getName());
                jsonObject.put("email", myPageDTO.getEmail());
                jsonObject.put("interest1", myPageDTO.getInterest1());
                jsonObject.put("interest2", myPageDTO.getInterest2());
                jsonObject.put("password",myPageDTO.getPassword());

            }

            return jsonObject.toJSONString();
        }

        @PostMapping("/{user_id}/post")
        public String myPageModify(@RequestBody JSONObject jsonObject,@PathVariable("user_id") String userId){
            MyPageDTO myPageDTO = new MyPageDTO();
            String role = service.check_role(userId);

            myPageDTO.setUser_id(userId);
            myPageDTO.setName(jsonObject.get("$name").toString());
            myPageDTO.setInterest1(jsonObject.get("$interest1").toString());
            myPageDTO.setInterest2(jsonObject.get("$interest2").toString());

            if(role.equals("tutee")){
                service.modify_tutee(myPageDTO);
            }else if(role.equals("tutor")){
                myPageDTO.setProfileimg(jsonObject.get("$profileImg").toString());
                myPageDTO.setNationality(jsonObject.get("$nationality").toString());
                service.modify_tutor(myPageDTO);
            }
            return null;
        }

        @PostMapping("/{user_id}/delete")
        public String myPageDelete(@PathVariable("user_id") String userId){
            String role = service.check_role(userId);
            if(role.equals("tutee")){
                service.delete_tutee(userId);
            }else if(role.equals("tutor")){
                service.delete_tutor(userId);
            }

            return null;
        }

        @GetMapping("/{user_id}/schedule/get")
        public String schedulePage(@PathVariable("user_id") String userId) {
            MypageScheduleDTO mypageScheduleDTO = service.tuteeProfile(userId);
            List<ClassDTO> classDTOList = service.schedule_tutee(userId);

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tutee_name",mypageScheduleDTO.getName());

            for(ClassDTO dto :  classDTOList){
                JSONObject object = new JSONObject();
                object.put("class_id",dto.getClass_id());
                object.put("class_date",dto.getClass_date());
                object.put("class_time",dto.getClass_time());
                object.put("tutor_name",dto.getName());
                object.put("class_id",dto.getClass_id());

                jsonArray.add(object);
            }
            jsonObject.put("schedules",jsonArray);
            String jsonString = jsonObject.toString();

            return jsonString;
        }

        @PutMapping("/{user_id}/schedule/cancel")
        public String scheduleCancel(@RequestBody JSONObject jsonObject) {
            service.schedule_cancel(jsonObject.get("$class_id").toString());
//            String class_id = jsonObject.get("$class_id").toString();
//            System.out.println(class_id);
            return null;
        }

        @GetMapping("/{user_id}/schedule/getCalendar")
        public String schedulePageCalendar(@PathVariable("user_id") String tutorId) {
            MypageScheduleDTO profileDto = service.tutorProfile(tutorId);
            List<ClassDTO> scheduleDTOList = service.schedule_tutor(tutorId);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tutor_id",profileDto.getUser_id());
            jsonObject.put("name",profileDto.getName());
            jsonObject.put("profileimg",profileDto.getProfileimg());

            JSONArray jsonArray = new JSONArray();
            for(ClassDTO dto : scheduleDTOList){
                JSONObject jsonObject2 =new JSONObject();
                jsonObject2.put("class_id",dto.getClass_id());
                jsonObject2.put("class_date",dto.getClass_date());
                jsonObject2.put("class_time",dto.getClass_time());
                jsonObject2.put("tutee_id",dto.getTutee_id());
                jsonObject2.put("tutee_name",dto.getName());
                jsonObject2.put("tutor_id",dto.getTutor_id());

                jsonArray.add(jsonObject2);
            }

            jsonObject.put("schedule",jsonArray);
            String jsonString = jsonObject.toJSONString();

            return jsonString;
        }

        @PostMapping("/{user_id}/schedule/postCalender")
        public String schedulePost(@RequestBody JSONObject jsonObject) {
            String tutor_id = jsonObject.get("$tutor_id").toString();
            System.out.println(tutor_id);
            return tutor_id;
        }

        @GetMapping("/{user_id}/history/get")
        public String historyPage(@PathVariable("user_id") String user_id) {
            MypageScheduleDTO mypageScheduleDTO = service.tuteeProfile(user_id);
            List<ClassDTO> dtoList = service.history_tutee(user_id);

            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            jsonObject.put("name",mypageScheduleDTO.getName());
            jsonObject.put("user_id",user_id);

            for(ClassDTO dto : dtoList){
                JSONObject object = new JSONObject();
                object.put("class_date",dto.getClass_date());
                object.put("tutor_name",dto.getName());
                object.put("memo", null);

                jsonArray.add(object);
            }

            jsonObject.put("history",jsonArray);
            String jsonString = jsonObject.toString();

            return jsonString;
        }
    }
}

