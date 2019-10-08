package com.revature.assignforce.assignforcefilehandler.controller;

import com.revature.assignforce.assignforcefilehandler.service.FileService;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import io.swagger.annotations.*;

/**
 * Controller for handling Files, and depositing them into AWS:S3.
 * 
 * @author devon
 *
 */
@RestController
@Api(value = "/api/filehandler", tags = { "Filehandler Controller"})
public class FileController {

	private FileService fileService;

	@Autowired
	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	/**
	 * Accepts a file and metadata (?) from the request body. Passes the file into
	 * the FileService to save to AWS:S3.
	 * 
	 * TODO: switch from temporary key to metadata.
	 * @param trainer 	Trainer who wants to add file
	 * @param file 		MultipartFile
	 * @param category 	File category
	 * @return The name of the file added
	 */
	@PostMapping("/")
	@ApiOperation(value = "Accepts a file and metadata from the request body. Passes the file into the FileService to save to AWS:S3.", 
	notes = "Takes in MultipartFile, Category (String), and Trainer ID (int). Returns the Key for the file to retrieve from S3.", 
	response = String.class)
	@ApiResponse(code = 200, message = "OK", response = String.class)
	public String addFile(@ApiParam(name = "file", type = "string") @RequestParam("file") MultipartFile file, @RequestParam("category") String category,
			@RequestParam("trainer") int trainer) {
		try {
			return fileService.save(file, category, trainer);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns a file retrieved by FileService based on the input key.
	 * @param key Get a file based on the key
	 * @return ResponseEntity of byte[]
	 */
	@GetMapping(value = "/")
	@ApiOperation(value = "Returns a Response containing a File retrieved AWS:S3.", 
	notes = "Takes in the file's key. If successful, returns a 200 response containing the file as a byte array. Otherwise, it returns a 400 response.", 
	consumes = "String", response = ResponseEntity.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Bad Request"), 
            @ApiResponse(code = 200, message = "OK", response = byte[].class)})
	public ResponseEntity<byte[]> getFile(@RequestParam(value = "key") String key) {
		try (InputStream inputStream = fileService.get(key).getObjectContent()) {
			byte[] media = IOUtils.toByteArray(inputStream);

			return new ResponseEntity<>(media, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Deletes a file based on the input key.
	 * 
	 * @param key delete a file based on the key
	 * @return returns true if file is successfully deleted
	 */
	@DeleteMapping("/")
	@ApiOperation(value = "Deletes a file from AWS:S3 bucket based on the input key.", notes = "Takes in the file's key. If successful, returns true. Otherwise, it returns false.", consumes = "String", response = Boolean.class)
	@ApiResponse(code = 201, message = "Created", response = boolean.class)
	public boolean deleteFile(@RequestParam(value = "key") String key) {
		return fileService.delete(key);
	}
}
