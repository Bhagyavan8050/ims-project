package com.ims.backend.controller;

import com.ims.backend.entity.WorkItem;
import com.ims.backend.repository.WorkItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class WorkItemController {

    @Autowired
    private WorkItemRepository workItemRepository;

    //  1. GET ALL INCIDENTS
    @GetMapping("/incidents")
    public List<WorkItem> getAllIncidents() {
        return workItemRepository.findAll();
    }

    //  2. SAVE RCA + CALCULATE MTTR (ONLY ONE API)
    @PostMapping("/rca/{id}")
    public String saveRCA(@PathVariable Long id,
                          @RequestBody Map<String, String> data) {

        WorkItem item = workItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        //  Save RCA
        item.setRca(data.get("rca"));

        //  Convert time from frontend
        long start = Long.parseLong(data.get("startTime"));
        long end = Long.parseLong(data.get("endTime"));

        //  Calculate MTTR
        long mttr = end - start;

        item.setCreatedTime(start);
        item.setResolvedTime(end);
        item.setMttr(mttr);

        //  Update status
        item.setStatus("RESOLVED");

        //  Save to DB
        workItemRepository.save(item);

        return "RCA + MTTR saved successfully";
    }
}