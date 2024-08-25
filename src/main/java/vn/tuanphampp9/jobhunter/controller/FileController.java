package vn.tuanphampp9.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.tuanphampp9.jobhunter.domain.Response.file.ResUploadFileDTO;
import vn.tuanphampp9.jobhunter.service.FileService;
import vn.tuanphampp9.jobhunter.util.annotation.ApiMessage;
import vn.tuanphampp9.jobhunter.util.error.FileUploadException;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    @Value("${tuanpp9.upload-file.base-uri}")
    private String baseURI;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload file")
    public ResponseEntity<ResUploadFileDTO> upload(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, FileUploadException {

        // validate file(file not null, file extension, file size)
        if (file.isEmpty() || file == null) {
            throw new FileUploadException("File is empty");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(suffix -> fileName.endsWith(suffix));
        if (!isValid) {
            throw new FileUploadException("File extension is not valid. Only allows [pdf, jpg, jpeg, png, doc, docx]");
        }
        // create folder if not exist
        this.fileService.createDirectory(baseURI + folder);

        // save file
        String uploadFile = this.fileService.store(file, folder);
        ResUploadFileDTO resUploadFileDTO = new ResUploadFileDTO(uploadFile, Instant.now());
        return ResponseEntity.ok().body(resUploadFileDTO);
    }

}
