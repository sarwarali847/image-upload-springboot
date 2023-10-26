package com.imageupload.imageuploadspringboot.service;

import com.imageupload.imageuploadspringboot.model.Constant;
import com.imageupload.imageuploadspringboot.model.LoadFile;
import com.mongodb.BasicDBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class fileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    Logger logger = LoggerFactory.getLogger(fileService.class);
    public String addFile(MultipartFile file) throws IOException {
        logger.info("Add file method invoked with file name {}",file.getOriginalFilename());
        BasicDBObject metaData = new BasicDBObject();
        metaData.put("fileSize", file.getSize());
        Object fileID = gridFsTemplate.store(file.getInputStream() , file.getOriginalFilename(), file.getContentType(), metaData);
        logger.info("File Uploaded Successfully and returned id is : {}",fileID.toString());
        return fileID.toString();
    }


    public LoadFile downloadFile(String id) throws IOException {
        logger.info("Download file method invoked with id {}",id);
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where(Constant.ID).is(id)) );

        LoadFile loadFile = new LoadFile();

        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
            loadFile.setFileName(gridFSFile.getFilename() );

            loadFile.setFileType(gridFSFile.getMetadata().get(Constant.CONTENT_TYPE).toString() );

            loadFile.setFileSize(gridFSFile.getMetadata().get(Constant.FILE_SIZE).toString() );

            loadFile.setFile(IOUtils.toByteArray(gridFsOperations.getResource(gridFSFile).getInputStream()) );
        }
        logger.info("Successfully fetched file with id {} and returned file size {} and type {}", id, loadFile.getFileSize(),loadFile.getFileType());
        return loadFile;
    }

    public List<LoadFile> getAllFileDetails() throws IOException {
        logger.info("Getting details for all stored files.");

        List<LoadFile> fileDetailsList = new ArrayList<>();
        List<GridFSFile> gridFSFiles = new ArrayList<>();
        GridFSFindIterable gridFSFileList = gridFsTemplate.find(new Query());
        for (GridFSFile gridFSFile : gridFSFileList) {
            gridFSFiles.add(gridFSFile);
        }


        for (GridFSFile gridFSFile : gridFSFiles) {
            LoadFile loadFile = new LoadFile();

            if (gridFSFile.getMetadata() != null) {
                loadFile.setFileName(gridFSFile.getFilename());
                loadFile.setFileType(gridFSFile.getMetadata().get(Constant.CONTENT_TYPE).toString());
                loadFile.setFileSize(gridFSFile.getMetadata().get(Constant.FILE_SIZE).toString());
                loadFile.setId(String.valueOf(gridFSFile.getId().asObjectId().getValue()));
                loadFile.setFile(IOUtils.toByteArray(gridFsOperations.getResource(gridFSFile).getInputStream()));
                fileDetailsList.add(loadFile);
            }
        }

        logger.info("Successfully fetched details for {} stored files.", fileDetailsList.size());
        return fileDetailsList;
    }
}
