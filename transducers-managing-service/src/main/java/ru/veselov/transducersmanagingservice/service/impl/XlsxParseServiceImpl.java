package ru.veselov.transducersmanagingservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.transducersmanagingservice.exception.NotCorrectTableExcelTableFormatException;
import ru.veselov.transducersmanagingservice.exception.ParseXlsxFileException;
import ru.veselov.transducersmanagingservice.service.XlsxParseService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class XlsxParseServiceImpl implements XlsxParseService {

    @Override
    public List<String> parseSerials(MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<String> serials = new ArrayList<>();
            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                CellType cellType = cell.getCellType();
                String value;
                if (cellType == CellType.STRING) {
                    value = cell.getStringCellValue();
                } else if (cellType == CellType.NUMERIC) {
                    value = new DataFormatter().formatCellValue(cell);
                } else {
                    String error = "Not correct type of cell, should be String or Numeric";
                    log.error(error);
                    throw new NotCorrectTableExcelTableFormatException(error);
                }
                if (StringUtils.isNotBlank(value)) {
                    serials.add(value);
                }
            }
            log.info("Serial numbers successfully parsed: [total: {}]", serials.size());
            return serials;
        } catch (IOException | POIXMLException | NotOfficeXmlFileException e) {
            log.error("Error during parsing xlsx file: {}", e.getMessage());
            throw new ParseXlsxFileException(e.getMessage(), e);
        }

    }
}
