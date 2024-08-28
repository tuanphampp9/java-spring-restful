package vn.tuanphampp9.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.tuanphampp9.jobhunter.domain.Job;
import vn.tuanphampp9.jobhunter.domain.Permission;
import vn.tuanphampp9.jobhunter.domain.Response.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.service.PermissionService;
import vn.tuanphampp9.jobhunter.util.annotation.ApiMessage;
import vn.tuanphampp9.jobhunter.util.error.IdInvalidException;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create new permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        boolean isExist = this.permissionService.handleExistsPermission(permission.getModule(), permission.getApiPath(),
                permission.getMethod());
        if (isExist) {
            throw new IdInvalidException("Permission already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.permissionService.handleCreatePermission(permission));
    }

    @PutMapping("permissions")
    @ApiMessage("Update permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        Permission permissionFound = this.permissionService.handleFindPermissionById(permission.getId());
        if (permissionFound == null) {
            throw new IdInvalidException("Permission not found");
        }

        boolean isExist = this.permissionService.handleExistsPermission(permission.getModule(), permission.getApiPath(),
                permission.getMethod());
        boolean isSameName = this.permissionService.isSameName(permission);
        if (isExist && isSameName) {
            throw new IdInvalidException("Permission already exists");
        }
        permissionFound.setName(permission.getName());
        permissionFound.setModule(permission.getModule());
        permissionFound.setApiPath(permission.getApiPath());
        permissionFound.setMethod(permission.getMethod());
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.permissionService.handleUpdatePermission(permissionFound));
    }

    @GetMapping("/permissions")
    @ApiMessage("Get all permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermission(@Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.permissionService.handleFindAllPermission(spec, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws IdInvalidException {
        Permission permission = this.permissionService.handleFindPermissionById(id);
        if (permission == null) {
            throw new IdInvalidException("Permission not found");
        }
        this.permissionService.handleDeletePermission(id);
        return ResponseEntity.ok().body(null);
    }

}
