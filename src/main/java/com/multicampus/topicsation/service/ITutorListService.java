package com.multicampus.topicsation.service;

import com.multicampus.topicsation.dto.TutorScheduleDTO;
import com.multicampus.topicsation.dto.TutorViewDTO;

import java.util.List;
import java.util.Map;

public interface ITutorListService {
    TutorViewDTO tutorInfo(Map<String,Object> paramMap);
    boolean ClassReservate(Map <String,Object> paramMap);
}
