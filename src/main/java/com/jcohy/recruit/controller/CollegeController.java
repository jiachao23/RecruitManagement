package com.jcohy.recruit.controller;

import com.jcohy.lang.StringUtils;
import com.jcohy.recruit.common.JsonResult;
import com.jcohy.recruit.model.*;
import com.jcohy.recruit.service.CollegeService;
import com.jcohy.recruit.service.DeliveryRecordService;
import com.jcohy.recruit.service.JobService;
import com.jcohy.recruit.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/college")
public class CollegeController {

    @Autowired
    private CollegeService collegeService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private DeliveryRecordService deliveryRecordService;


    /**
     * 学院登陆接口
     * @param num 登录号
     * @param password 密码
     * @return
     */
    @GetMapping("/login")
    public JsonResult login(Integer num, String password){
        College login = null;
        try {
            if(num == null || StringUtils.isEmpty(password)){
                return JsonResult.fail("用户名或者密码不能为空");
            }

            login = collegeService.login(num, password);

            if(login == null){
                return JsonResult.fail("登录失败,用户名不存在");
            }
            if(!login.getPassword().equals(password)){
                return JsonResult.fail("登录失败,用户名账号密码不匹配");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResult.ok("登录成功").set("data",login);
    }

    /**
     * 学院注册接口
     * @param num 账号 必填
     * @param phone 电话 必填
     * @param password 密码 必填
     * @param name 姓名 必填
     * @return
     */
    @GetMapping("/register")
    public JsonResult register(Integer num, String phone, String password, String name){
        if(num == null||phone == null || StringUtils.hashEmpty(name,password)){
            return JsonResult.fail("参数不能为空");
        }
        boolean exist = collegeService.checkUser(num);
        if(exist){
            return JsonResult.fail("用户已存在");
        }
        College college = new College();
        college.setNum(num);
        college.setPassword(password);
        college.setName(name);
        college.setCollegeTel(phone);
        collegeService.saveOrUpdate(college);
        return JsonResult.ok("注册成功").set("data", college);
    }

    /**
     * 更新学院信息
     * @param college
     * @return
     */
    @GetMapping("/update")
    public JsonResult update(College college){
        try {
            College coll = collegeService.saveOrUpdate(college);
            return JsonResult.ok().set("data", coll);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.fail(e.getMessage());
        }
    }

    /**
     * 发布job
     * @param job
     * @return
     */
    @GetMapping("/addJob")
    public JsonResult addJob(Job job){
        try {
            Job j = jobService.saveOrUpdate(job);
            return JsonResult.ok("发布成功").set("data", j);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.fail(e.getMessage());
        }
    }

    /**
     * 删除job
     * @param job
     * @return
     */
    @GetMapping("/deleteJob")
    public JsonResult deleteJob(Job job){
        try {
            jobService.delete(job.getId());
            return JsonResult.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.fail(e.getMessage());
        }
    }

    /**
     * 获取全部投递
     * @param id 学院id
     * @return
     */
    @GetMapping("/deliverys")
    public JsonResult findAllDeliverys(Integer id){
        List<DeliveryRecord> all = deliveryRecordService.findAll();
        List<DeliveryRecord> mine = new ArrayList<>();
        for (DeliveryRecord deliveryRecord : all) {
            if (deliveryRecord.getCollegeId().equals("id") && !deliveryRecord.getStatus().equals("5")) {
                mine.add(deliveryRecord);
            }
        }
        return JsonResult.ok("获取成功").set("data", mine);
    }

    /**
     * 更新投递记录
     * @param deliveryRecord
     * @return
     */
    @GetMapping("/updateDelivery")
    public JsonResult updateDelivery(DeliveryRecord deliveryRecord){
        try {
            DeliveryRecord delivery = deliveryRecordService.saveOrUpdate(deliveryRecord);
            return JsonResult.ok("更新成功").set("data", delivery);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.fail(e.getMessage());
        }
    }


    /**
     * 更新简历状态为删除
     * @param id
     * @return
     */
    @GetMapping("/updateResume")
    public JsonResult updateResume(Integer id){
        try {
            DeliveryRecord deliveryRecord = deliveryRecordService.findById(id);
            Resume resume = resumeService.findById(deliveryRecord.getResumeId());
            resume.setStatus(0);
            resumeService.saveOrUpdate(resume);
            deliveryRecord.setStatus(5);
            deliveryRecordService.saveOrUpdate(deliveryRecord);
            return JsonResult.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.fail(e.getMessage());
        }
    }




}