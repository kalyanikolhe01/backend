package com.dhundhoo.acendMarketing.service;
import com.dhundhoo.acendMarketing.dto.PublicationRequestDTO;
import com.dhundhoo.acendMarketing.dto.PublicationResponseDTO;
import com.dhundhoo.acendMarketing.enums.UserRole;
import com.dhundhoo.acendMarketing.model.Publication;
import com.dhundhoo.acendMarketing.model.User;
import com.dhundhoo.acendMarketing.repository.PublicationRepository;
import com.dhundhoo.acendMarketing.repository.UserRepository;
import com.dhundhoo.acendMarketing.utility.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PublicationService {
    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    //add publication api
    public void addPublication(PublicationRequestDTO request, String sessionToken) {
        // Extract session details
        Claims claims = jwtTokenUtil.parseToken(sessionToken);
        String userId = claims.get("userId", String.class);
        String userRole = claims.get("userRole", String.class);

        // Verify SUPER_ADMIN or ADMIN role
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty() || !(userRole.equals("SUPER_ADMIN") || userRole.equals("ADMIN"))) {
            throw new RuntimeException("Unauthorized: Only SUPER_ADMIN or ADMIN can add publications.");
        }

        // Save publication
        Publication publication = new Publication();
        BeanUtils.copyProperties(request, publication); // Map DTO to entity
        publicationRepository.save(publication);
    }




    //get all publication api base on thier roles

    public List<PublicationResponseDTO> getAllPublications(String sessionToken) {
        // Extract userId from session token
        String userId = jwtTokenUtil.extractUserIdFromSession(sessionToken);
        if (userId == null) {
            throw new IllegalArgumentException("Invalid session or user not found.");
        }

        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        User user = userOptional.get();
        Integer userMargin = user.getMargin();
        UserRole userRole = user.getUserRole();
        List<Publication> publications = publicationRepository.findAll();
        return publications.stream()
                .map(publication -> {
                    PublicationResponseDTO dto = new PublicationResponseDTO();
                    BeanUtils.copyProperties(publication, dto);
                    if (userRole == UserRole.CLIENT && userMargin != null) {
                        float marginAmount = (userMargin / 100.0f) * publication.getOrgPrice();
                        float adjustedPrice = publication.getOrgPrice() + marginAmount;
                        dto.setMarginPrice(adjustedPrice);
                    } else {
                        dto.setMarginPrice(publication.getOrgPrice());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }



    //delete pubalifation by superadmin api
    public void deletePublication(String userId, String sessionToken) {
        Claims claims;
        try {
            claims = jwtTokenUtil.parseToken(sessionToken);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired session token.");
        }
        String userRole = claims.get("userRole", String.class);
        if (!"SUPER_ADMIN".equals(userRole) && !"ADMIN".equals(userRole)) {
            throw new RuntimeException("Unauthorized: Only SUPER_ADMIN or ADMIN can delete publications.");
        }
        if (!publicationRepository.existsByUserId(userId)) {
            throw new RuntimeException("Publication with the given userId not found.");
        }
        publicationRepository.deleteByUserId(userId);
    }


    //edit user api
    public void editPublication(String userId, String sessionToken, PublicationRequestDTO publicationRequestDTO) {
        Claims claims = jwtTokenUtil.parseToken(sessionToken);
        String userRole = claims.get("userRole", String.class);
        if (!Set.of("ADMIN", "SUPER_ADMIN").contains(userRole)) {
            throw new RuntimeException("Unauthorized: Only ADMIN or SUPER_ADMIN can edit publications.");
        }
        Publication publication = publicationRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Publication with the given userId not found."));
        updatePublicationDetails(publication, publicationRequestDTO);
        publicationRepository.save(publication);
    }

    private void updatePublicationDetails(Publication publication, PublicationRequestDTO dto) {
        publication.setPublication(dto.getPublication());
        publication.setOrgPrice(dto.getOrgPrice());
        publication.setMarginPrice(dto.getMarginPrice());
        publication.setUsdPrice(dto.getUsdPrice());
        publication.setWordsAllowed(dto.getWordsAllowed());
        publication.setBacklinksAllowed(dto.getBacklinksAllowed());
        publication.setTat(dto.getTat());
        publication.setSponsored(dto.isSponsored());
        publication.setIndexed(dto.isIndexed());
        publication.setDoFollow(dto.isDoFollow());
        publication.setGenres(dto.getGenres());
        publication.setSample(dto.getSample());
        publication.setDaChecker(dto.getDaChecker());
        publication.setTrafficChecker(dto.getTrafficChecker());
    }



    //add excel sheet

    public void addBulkPublications(MultipartFile file, String sessionToken) {
        // Validate the user's role
        validateUserRole(sessionToken);
        List<PublicationRequestDTO> publications = parseExcelFile(file);
        savePublications(publications);
    }
    private void validateUserRole(String sessionToken) {
        Claims claims = jwtTokenUtil.parseToken(sessionToken);
        String userId = claims.get("userId", String.class);
        String userRole = claims.get("userRole", String.class);
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty() || !(userRole.equals("SUPER_ADMIN") || userRole.equals("ADMIN"))) {
            throw new RuntimeException("Unauthorized: Only SUPER_ADMIN or ADMIN can add publications.");
        }
    }
    private List<PublicationRequestDTO> parseExcelFile(MultipartFile file) {
        List<PublicationRequestDTO> publications = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            rows.next();
            while (rows.hasNext()) {
                Row row = rows.next();
                PublicationRequestDTO dto = extractPublicationDTO(row);
                publications.add(dto);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while reading the Excel file: " + e.getMessage(), e);
        }
        return publications;
    }

    private PublicationRequestDTO extractPublicationDTO(Row row) {
        PublicationRequestDTO dto = new PublicationRequestDTO();
        dto.setPublication(getStringCellValue(row, 0));
        dto.setOrgPrice(getFloatCellValue(row, 1));
        dto.setMarginPrice(getFloatCellValue(row, 2));
        dto.setUsdPrice(getFloatCellValue(row, 3));
        dto.setWordsAllowed(getIntCellValue(row, 4));
        dto.setBacklinksAllowed(getStringCellValue(row, 5));
        dto.setTat(getIntCellValue(row, 6));
        dto.setSponsored(getBooleanCellValue(row, 7));
        dto.setIndexed(getBooleanCellValue(row, 8));
        dto.setDoFollow(getBooleanCellValue(row, 9));
        dto.setGenres(getStringCellValue(row, 10));
        dto.setSample(getStringCellValue(row, 11));
        dto.setDaChecker(getStringCellValue(row, 12));
        dto.setTrafficChecker(getStringCellValue(row, 13));
        return dto;
    }

    private String getStringCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return cell.getStringCellValue().trim();
    }

    private float getFloatCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return (float) cell.getNumericCellValue();
    }
    private int getIntCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return (int) cell.getNumericCellValue();
    }
    private boolean getBooleanCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return cell.getBooleanCellValue();
    }
    private void savePublications(List<PublicationRequestDTO> publications) {
        for (PublicationRequestDTO publicationRequest : publications) {
            Publication publication = new Publication();
            BeanUtils.copyProperties(publicationRequest, publication);
            publicationRepository.save(publication);
        }
    }



    //download all data in excel
//    public void downloadPublications(String sessionToken, HttpServletResponse response) {
//        // Validate user role
//        validateUserRole(sessionToken);
//
//        // Export publications to Excel
//        exportPublicationsToExcel(response);
//    }
//
//    public void exportPublicationsToExcel(HttpServletResponse response) {
//        // Set the content type and attachment header
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader("Content-Disposition", "attachment; filename=publications.xlsx");
//
//        try (Workbook workbook = new XSSFWorkbook()) {
//            Sheet sheet = workbook.createSheet("Publications");
//
//            // Create header row
//            Row headerRow = sheet.createRow(0);
//            String[] headers = {
//                    "Publication", "Original Price", "Margin Price", "USD Price", "Words Allowed",
//                    "Backlinks Allowed", "TAT", "Sponsored", "Indexed", "DoFollow", "Genres",
//                    "Sample", "DA Checker", "Traffic Checker"
//            };
//
//            for (int i = 0; i < headers.length; i++) {
//                Cell cell = headerRow.createCell(i);
//                cell.setCellValue(headers[i]);
//                CellStyle style = workbook.createCellStyle();
//                Font font = workbook.createFont();
//                font.setBold(true);
//                style.setFont(font);
//                cell.setCellStyle(style);
//            }
//
//            // Fetch data from the database
//            List<Publication> publications = publicationRepository.findAll();
//
//            // Populate rows with data
//            int rowIndex = 1;
//            for (Publication publication : publications) {
//                Row row = sheet.createRow(rowIndex++);
//
//                row.createCell(0).setCellValue(publication.getPublication());
//                row.createCell(1).setCellValue(publication.getOrgPrice());
//                row.createCell(2).setCellValue(publication.getMarginPrice());
//                row.createCell(3).setCellValue(publication.getUsdPrice());
//                row.createCell(4).setCellValue(publication.getWordsAllowed());
//                row.createCell(5).setCellValue(publication.getBacklinksAllowed());
//                row.createCell(6).setCellValue(publication.getTat());
//                row.createCell(7).setCellValue(publication.isSponsored());
//                row.createCell(8).setCellValue(publication.isIndexed());
//                row.createCell(9).setCellValue(publication.isDoFollow());
//                row.createCell(10).setCellValue(publication.getGenres());
//                row.createCell(11).setCellValue(publication.getSample());
//                row.createCell(12).setCellValue(publication.getDaChecker());
//                row.createCell(13).setCellValue(publication.getTrafficChecker());
//            }
//
//            // Write to response output stream
//            workbook.write(response.getOutputStream());
//        } catch (IOException e) {
//            throw new RuntimeException("Error while generating Excel file: " + e.getMessage(), e);
//        }
//    }

}
