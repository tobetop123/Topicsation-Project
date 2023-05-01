package com.multicampus.topicsation.controller;

import com.multicampus.topicsation.dto.ClassDTO;
import com.multicampus.topicsation.dto.MyPageDTO;
import com.multicampus.topicsation.dto.MypageScheduleDTO;
import com.multicampus.topicsation.service.IMyPageService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        @GetMapping("/download/{fileName}")
        public ResponseEntity<Resource> downloadFile(@PathVariable("fileName") String fileName) throws IOException {
            // 파일 경로를 수정하여 실제 파일의 위치를 지정해주세요.
            String filePath = "src/main/resources/static/assets/certificate/"+fileName;
            File file = new File(filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(file.getName()).build());

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
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
                jsonObject.put("memo",myPageDTO.getInfo());
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
                myPageDTO.setGender(jsonObject.get("$gander").toString());
                myPageDTO.setNationality(jsonObject.get("$nationality").toString());
                myPageDTO.setInfo(jsonObject.get("$memo").toString());
                service.modify_tutor(myPageDTO);
            }
            return null;
        }

        @PostMapping("/{user_id}/profileUpdate")
        public ResponseEntity<?> mypageProfile(@PathVariable("user_id") String userId, @RequestParam("file") MultipartFile file)
        throws IOException {

            final String UPLOAD_DIR = "src/main/resources/static/assets/img/profile/";

            try {
                // 파일이 비어있는지 확인
                if (file.isEmpty()) {
                    return new ResponseEntity<>("파일을 선택해주세요.", HttpStatus.BAD_REQUEST);
                }

                System.out.println("file : " + file);

                // 파일 저장
                byte[] bytes = file.getBytes();
                String fileExtension = getFileExtension(file.getOriginalFilename());
                Path path = Paths.get(UPLOAD_DIR + userId + "." + fileExtension);

                service.chang_profileImg(userId);

                Files.write(path, bytes);

                // 프로필 정보 업데이트 로직 작성
                // 예를 들어, 사용자 정보를 데이터베이스에 업데이트하는 로직을 여기에 추가합니다.

                return new ResponseEntity<>(file.getOriginalFilename() + " 파일이 업로드되었습니다.", HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>("파일 업로드 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        private String getFileExtension(String fileName) {
            int lastIndexOfDot = fileName.lastIndexOf(".");
            if (lastIndexOfDot == -1) {
                return ""; // 확장자가 없는 경우
            }
            return fileName.substring(lastIndexOfDot + 1);
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
        public String schedulePageCalendar(@PathVariable("user_id") String tutorId,
                                            @RequestParam("calendarDate") String calendarDate) {

            MypageScheduleDTO mypageScheduleDTO = new MypageScheduleDTO();
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("tutorId",tutorId);
            paramMap.put("classDate", calendarDate);

            mypageScheduleDTO = service.schedule_tutor(paramMap, mypageScheduleDTO);

            System.out.println(mypageScheduleDTO.getUser_id());
            System.out.println(mypageScheduleDTO.getName());
            System.out.println(mypageScheduleDTO.getProfileimg());

            JSONObject jsonObject_info = new JSONObject();
            JSONArray jsonArray_schedule = new JSONArray();

            jsonObject_info.put("user_id",tutorId);
            jsonObject_info.put("name", mypageScheduleDTO.getName());
            jsonObject_info.put("picture", mypageScheduleDTO.getProfileimg());

            for(int i = 0; i<mypageScheduleDTO.getScheduleDTOList().size(); i++) {
                JSONObject jsonObject_schedule = new JSONObject();
                jsonObject_schedule.put("class_id", mypageScheduleDTO.getScheduleDTOList().get(i).getClass_id());
                jsonObject_schedule.put("class_date", mypageScheduleDTO.getScheduleDTOList().get(i).getClass_date());
                jsonObject_schedule.put("class_time", mypageScheduleDTO.getScheduleDTOList().get(i).getClass_time());
                jsonObject_schedule.put("tutee_id", mypageScheduleDTO.getScheduleDTOList().get(i).getTutee_id());
                jsonObject_schedule.put("tutor_id", mypageScheduleDTO.getScheduleDTOList().get(i).getTutor_id());
                jsonObject_schedule.put("name", mypageScheduleDTO.getScheduleDTOList().get(i).getName());
                jsonArray_schedule.add(jsonObject_schedule);
            }
            
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("tutor_info", jsonObject_info);
            jsonObject.put("schedule", jsonArray_schedule);

            String jsonString = jsonObject.toJSONString();
            System.out.println(jsonString);

            return jsonString;
        }

        @PostMapping("/{user_id}/schedule/postCalender")
        public String schedulePost(@RequestBody String jsonString) {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject;
            JSONObject jsonUserInfo;
            JSONArray jsonSchedule;
            int result = 0;
            try {
                jsonObject = (JSONObject) parser.parse(jsonString);
                jsonUserInfo = (JSONObject) jsonObject.get("user_info");
                jsonSchedule = (JSONArray) jsonObject.get("schedule");
                System.out.println("jsonUserInfo : " + jsonUserInfo);
                System.out.println("jsonSchedule : " + jsonSchedule);
                result = service.scheduleUpdate(jsonUserInfo, jsonSchedule);
            } catch (Exception e){
                e.printStackTrace();
            }
            if(result == 1 || result == 0)
                return "success";
            else
                return "fail";
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

