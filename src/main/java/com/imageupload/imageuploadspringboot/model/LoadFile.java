package com.imageupload.imageuploadspringboot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoadFile {
    private String fileName;
    private String fileType;
    private String fileSize;
    private String id;
    byte[] file;
}
