package com.multicampus.topicsation.controller;

import com.multicampus.topicsation.dto.MyPageDTO;
import com.multicampus.topicsation.dto.TutorMypageScheduleDTO;
import com.multicampus.topicsation.dto.TutorScheduleDTO;
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
            System.out.println("실행");
            List<MyPageDTO> list =service.view_admin();
            JSONArray jsonArray = new JSONArray();

            for (MyPageDTO dto : list){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tutorName",dto.getName());
                jsonObject.put("approlDate",dto.getRegi_date());
                jsonObject.put("file",dto.getCertificate());

                jsonArray.add(jsonObject);
            }

            String jsonString = jsonArray.toString();
            System.out.println(jsonString);

            return jsonString;
        }

        @GetMapping("/{user_id}/get")
        public String myPage(@PathVariable("user_id") String userId) {
            MyPageDTO myPageDTO;
            JSONObject jsonObject = new JSONObject();
            String role = service.check_role(userId);
            if (role.equals("tutor")) {
                myPageDTO = service.view_tutor(userId);
                //System.out.println(tutorId);

                jsonObject.put("profileImg", myPageDTO.getProfileimg());
                jsonObject.put("name", myPageDTO.getName());
                jsonObject.put("email", myPageDTO.getEmail());
                jsonObject.put("nationality", myPageDTO.getNationality());
                jsonObject.put("interest1", myPageDTO.getInterest1());
                jsonObject.put("interest2", myPageDTO.getInterest2());
                jsonObject.put("genderRadios", myPageDTO.getGender());

            } else if(role.equals("tutee")) {
                myPageDTO = service.view_tutee(userId);
                System.out.println(userId);
//                System.out.println(myPageDTO);
                jsonObject.put("tutor-name", myPageDTO.getName());
                jsonObject.put("name", myPageDTO.getName());
                jsonObject.put("email", myPageDTO.getEmail());
                jsonObject.put("interest1", myPageDTO.getInterest1());
                jsonObject.put("interest2", myPageDTO.getInterest2());

            }

            System.out.println(jsonObject);
            return jsonObject.toJSONString();
        }

        @PostMapping("/{user_id}/post")
        public String myPageModify(@RequestBody MyPageDTO myPageDTO){
            String role = myPageDTO.getRole();
            if(role.equals("tutee")){
                service.modify_tutee(myPageDTO);
            }else if(role.equals("tutor")){
                service.modify_tutor(myPageDTO);
            }
            return null;
        }


        @GetMapping("/{user_id}/schedule/get")
        public String schedulePage() {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();


//            jsonObject.put("tutee_name", "Tom Softy");
//            jsonObject.put("user_id", "1234");
//
//            JSONArray schedules = new JSONArray();
//
//            JSONObject scheduleObject1 = new JSONObject();
//            scheduleObject1.put("class_date", "2023-04-16");
//            scheduleObject1.put("class_time", "10:00AM");
//            scheduleObject1.put("tutor_name", "Jonny Dep");
//            scheduleObject1.put("class_id", "202304161000");
//            schedules.add(scheduleObject1);
//
//            JSONObject scheduleObject2 = new JSONObject();
//            scheduleObject2.put("class_date", "2023-04-18");
//            scheduleObject2.put("class_time", "11:30AM");
//            scheduleObject2.put("tutor_name", "Angeli Remy");
//            scheduleObject2.put("class_id", "202304181130");
//            schedules.add(scheduleObject2);
//
//            jsonObject.put("schedules", schedules);
//            jsonArray.add(jsonObject);
//
            String jsonString = jsonArray.toString();
            System.out.println(jsonString);

            return jsonString;
        }

        @PutMapping("/{user_id}/schedule/cancel")
        public String scheduleCancel(@RequestBody JSONObject jsonObject) {
            String class_id = jsonObject.get("$class_id").toString();
            System.out.println(class_id);
            return class_id;
        }

        @GetMapping("/{user_id}/schedule/getCalendar")
        public String schedulePageCalendar(@PathVariable("user_id") String tutorId) {
            TutorMypageScheduleDTO profileDto = service.tutorProfile(tutorId);
            List<TutorScheduleDTO> scheduleDTOList = service.schedule_tutor(tutorId);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tutor_id",profileDto.getTutor_id());
            jsonObject.put("name",profileDto.getName());
            jsonObject.put("profileimg",profileDto.getProfileimg());

            JSONArray jsonArray = new JSONArray();
            for(TutorScheduleDTO dto : scheduleDTOList){
                JSONObject jsonObject2 =new JSONObject();
                jsonObject2.put("class_id",dto.getClass_id());
                jsonObject2.put("class_date",dto.getClass_date());
                jsonObject2.put("class_time",dto.getClass_time());
                jsonObject2.put("tutee_id",dto.getTutee_id());
                jsonObject2.put("tutee_name",dto.getTutee_name());
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
        public String historyPage() {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            JSONObject obj1 = new JSONObject();
            obj1.put("class_date", "2023-04-16 10:00AM");
            obj1.put("tutor_name", "Jonny Dep");
            obj1.put("memo", "20200416.txt");
            jsonArray.add(obj1);

            JSONObject obj2 = new JSONObject();
            obj2.put("class_date", "2023-04-18 11:30AM");
            obj2.put("tutor_name", "Angeli Remy");
            obj2.put("memo", "20200418.txt");
            jsonArray.add(obj2);

            jsonObject.put("name", "김명진");
            jsonObject.put("user_id", "3125");
            jsonObject.put("history", jsonArray);

            String jsonString = jsonObject.toString();

            return jsonString;
        }
    }
}

