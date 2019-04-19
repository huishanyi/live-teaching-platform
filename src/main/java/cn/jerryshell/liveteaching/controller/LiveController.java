package cn.jerryshell.liveteaching.controller;

import cn.jerryshell.liveteaching.config.LiveServerConfig;
import cn.jerryshell.liveteaching.model.Live;
import cn.jerryshell.liveteaching.service.CourseService;
import cn.jerryshell.liveteaching.service.LiveService;
import cn.jerryshell.liveteaching.service.MajorService;
import cn.jerryshell.liveteaching.service.TeacherService;
import cn.jerryshell.liveteaching.vm.LiveViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Controller
public class LiveController {
    @Autowired
    private LiveService liveService;
    @Autowired
    private LiveServerConfig liveServerConfig;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private MajorService majorService;

    @GetMapping("/live")
    public String toLiveListPage(Model model) {
        List<Live> liveList = liveService.findAll();
        List<LiveViewModel> liveViewModelList = LiveViewModel.loadFromLiveList(liveServerConfig.getIp(), liveList, teacherService, courseService, majorService);
        model.addAttribute("liveViewModel", liveViewModelList);
        return "live-list";
    }

    @GetMapping("/live/{teacherId}/{roomName}")
    public String toLivePage(@PathVariable String teacherId,
                             @PathVariable String roomName,
                             Model model) {
        model.addAttribute("ip", liveServerConfig.getIp());
        model.addAttribute("port", liveServerConfig.getPort());
        model.addAttribute("teacherId", teacherId);
        model.addAttribute("roomName", roomName);
        return "live-watching";
    }

    @PostMapping("/live")
    public String createLive(Live live, HttpSession session) {
        live.setId(UUID.randomUUID().toString());
        live.setTeacherId(session.getAttribute("loginUserId").toString());
        liveService.save(live);
        return "redirect:/user";
    }

    @DeleteMapping("/live/{id}")
    public String deleteLiveById(@PathVariable String id) {
        liveService.deleteById(id);
        return "redirect:/user";
    }
}
