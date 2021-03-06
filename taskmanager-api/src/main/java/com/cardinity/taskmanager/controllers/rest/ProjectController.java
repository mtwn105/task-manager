package com.cardinity.taskmanager.controllers.rest;

import com.cardinity.taskmanager.dto.ProjectDTO;
import com.cardinity.taskmanager.dto.TaskDto;
import com.cardinity.taskmanager.entity.Project;
import com.cardinity.taskmanager.entity.Task;
import com.cardinity.taskmanager.service.ProjectService;
import com.cardinity.taskmanager.util.ProjectUtil;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author ahadahmed
 * @see <a href="#"> see this</a>
 */

@Tag(name = "Rest API for Projects")
@RestController
@RequestMapping("/api/v1")
@Validated
public class ProjectController {
    private ProjectService projectService;
    private ProjectUtil projectUtil;

    @Autowired
    public ProjectController(ProjectService projectService, ProjectUtil projectUtil){
        this.projectService = projectService;
        this.projectUtil = projectUtil;
    }

    @Operation(summary = "Get All Project", description = "API for getting all the  projects")
    @ApiResponse(content = {@Content(mediaType = "application/json"
                                    ,schema = @Schema(implementation = ProjectDTO.class))}
            ,responseCode = "200"
            , description = "Success response")
    @GetMapping("/project")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ADMIN')")
    @JsonView(value = {View.ProjectResponseView.class})
    public List<ProjectDTO> getAll() {
        ProjectDTO p = new ProjectDTO();
        List<ProjectDTO> projects = this.projectService.getAll();
        p.setProjectName("test-proj");
        return projects;
    }

    @Operation(summary = "Get a specific Project info", description = "API for getting information of a specific project by projectId")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/project/{projectId}")
    @JsonView(value ={View.ProjectResponseView.class})
    public ProjectDTO getProject(@PathVariable("projectId") @Min(value = 1, message = "project id must be greater than 0") long projectId) {
        Project project = this.projectService.getProject(projectId);
        ProjectDTO p = this.projectUtil.convertEntityToDto(project);
        return p;
    }



    @Operation(summary = "Get all task list of a specific Project", description = "API for getting task list of a specific project by projectId")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/project/{projectId}/tasks")
    @JsonView(value = View.TaskResponseView.class)
    public List<TaskDto> getProjectTasks(@PathVariable("projectId") @Min(value = 1, message = "project id must be greater than 0") long projectId) {
        Project project = this.projectService.getProject(projectId);
        ProjectDTO p = this.projectUtil.convertEntityToDto(project);
        return p.getTasks();
    }




    @Operation(summary = "Get all project list of a specific user", description = "API for getting all project of a specific user by  userId")
    @GetMapping("/projects/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(value ={View.ProjectResponseView.class})
    public List<ProjectDTO> getAllProjects(@PathVariable long userId){
        return this.projectService.getProjectsByUser(userId);
    }





    @Operation(summary = "Create Project", description = "API for create new project")
    @ApiResponse(content = {@Content(mediaType = "application/json"
//            ,schema = @Schema(implementation = ProjectDTO.class)
    )}
            ,responseCode = "200"
            , description = "Success response")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/project")
    @JsonView(View.ProjectResponseView.class)
    public ProjectDTO create(@RequestBody @JsonView(value = View.HttpMethodView.POST.class)
                                 @Valid ProjectDTO projectDTO){
         projectDTO = this.projectService.createProject(projectDTO);
        return projectDTO;
    }

    @Operation(summary = "Delete Project", description = "API for delete a project")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/project/{projectId}")
    @JsonView(value = {View.ProjectResponseView.class})
    public ProjectDTO removeProject(@PathVariable("projectId") @Min(value = 1, message = "project id must be greater than 0") long projectId){
        ProjectDTO projectDTO = this.projectService.deleteProject(projectId);
        return projectDTO;
    }

}
