package vn.tuanphampp9.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import io.micrometer.core.ipc.http.HttpSender.Response;
import jakarta.validation.Valid;
import vn.tuanphampp9.jobhunter.domain.Permission;
import vn.tuanphampp9.jobhunter.domain.Role;
import vn.tuanphampp9.jobhunter.domain.Response.ResultPaginationDTO;
import vn.tuanphampp9.jobhunter.service.RoleService;
import vn.tuanphampp9.jobhunter.util.error.IdInvalidException;

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
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role)
            throws IdInvalidException {
        boolean isExist = this.roleService.handleExistsRole(role.getName());
        if (isExist) {
            throw new IdInvalidException("Role already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.handleCreateRole(role));
    }

    @PutMapping("roles")
    public ResponseEntity<Role> update(@Valid @RequestBody Role role)
            throws IdInvalidException {
        Role roleFound = this.roleService.handleFindRoleById(role.getId());
        if (roleFound == null) {
            throw new IdInvalidException("Role not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.handleUpdateRole(role, roleFound));
    }

    @GetMapping("/roles")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(
            @Filter Specification<Role> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.handleFindAllRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id)
            throws IdInvalidException {
        Role role = this.roleService.handleFindRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role not found");
        }
        return ResponseEntity.ok().body(role);
    }

    @DeleteMapping("roles/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id)
            throws IdInvalidException {
        Role role = this.roleService.handleFindRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role not found");
        }
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.ok().body(null);
    }

}
