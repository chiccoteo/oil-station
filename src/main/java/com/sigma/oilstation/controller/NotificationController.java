package com.sigma.oilstation.controller;

import com.sigma.oilstation.payload.NotificationPostDTO;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.sigma.oilstation.utils.AppConstant.BASE_PATH;

@RequestMapping(BASE_PATH + NotificationController.NOTIFICATION_CONTROLLER)
public interface NotificationController {

    String NOTIFICATION_CONTROLLER = "/notification";

    String CREATE_NOT = "/create";

    String UPDATE_NOT = "/update";

    String GET = "/get";

    @PostMapping(CREATE_NOT)
    HttpEntity<?> create(@RequestBody NotificationPostDTO notificationPostDTO);

    @GetMapping(GET)
    HttpEntity<?> get();

    @PutMapping(UPDATE_NOT)
    HttpEntity<?> update(@RequestParam(name = "notificationId") UUID id);

}
