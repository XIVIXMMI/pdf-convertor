package com.omori.pdfconvertor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexExtractor {
    private static final Logger logger = LoggerFactory.getLogger(RegexExtractor.class);

    // Store patterns as constants for better maintenance
    private static final Map<String, Pattern> PATTERNS = new HashMap<>();

    static {
        PATTERNS.put("businessName", Pattern.compile("Tên kinh doanh \\(.*\\):\\s*(.+)"));
        PATTERNS.put("address", Pattern.compile("Địa chỉ lắp máy:\\s*(.+)"));
        PATTERNS.put("serialNumber", Pattern.compile("Số S/N của máy EDC:\\s*(\\S+)"));
        PATTERNS.put("posDevice", Pattern.compile("Loại máy:\\s*(.+)"));
        PATTERNS.put("groupName", Pattern.compile("Tên pháp lý \\(Theo giấy phép kinh doanh\\):(?:.*-\\s*(\\S+)|\\s*(.+))"));
        PATTERNS.put("notes", Pattern.compile("Ghi chú:\\s*([\\s\\S]+?)(?=\\nNgày|$)", Pattern.DOTALL));
        PATTERNS.put("merchantId", Pattern.compile("MID\\s+VND\\s+([\\d\\s\\n]+)"));
        PATTERNS.put("terminalId", Pattern.compile("TID\\s+VND\\s+([\\d\\s\\n]+)"));
    }

    public static String extractSpecificData(String text) {
        if (text == null || text.trim().isEmpty()) {
            logger.warn("Input text is null or empty");
            return "";
        }

        PDFData data = new PDFData();
        try {
            extractBusinessData(text, data);
            extractDeviceData(text, data);
            extractIdentificationData(text, data);

            logger.debug("Successfully extracted data for business: {}", data.getBusinessName());
            return data.toString();
        } catch (Exception e) {
            logger.error("Error extracting data from text", e);
            return "";
        }
    }

    private static void extractBusinessData(String text, PDFData data) {
        // Business name
        extractPattern("businessName", text).ifPresent(data::setBusinessName);
        // Address
        extractPattern("address", text).ifPresent(data::setAddress);
        // Group name
        Matcher groupMatcher = PATTERNS.get("groupName").matcher(text);
        if (groupMatcher.find()) {
            String groupName = groupMatcher.group(1) != null ?
                    groupMatcher.group(1).trim() :
                    groupMatcher.group(2).trim();
            data.setGroupName(groupName);
        }

        // Notes
        extractPattern("notes", text).ifPresent(notes -> {
            String cleanNotes = notes.trim();
            if (cleanNotes.startsWith("Ngày") || cleanNotes.isEmpty()) {
                data.setNotes("null");
            } else {
                // Preserve line breaks but clean up extra whitespace
                String processedNotes = cleanNotes.replaceAll("\\s+", " ").trim();
                data.setNotes(processedNotes);
            }
        });
    }

    private static void extractDeviceData(String text, PDFData data) {
        // Serial number
        extractPattern("serialNumber", text).ifPresent(data::setSerialNumber);
        // POS device
        extractPattern("posDevice", text).ifPresent(device ->
                data.setPosDevice(device.split("[^a-zA-Z0-9 ]+")[0].trim())
        );
    }

    private static void extractIdentificationData(String text, PDFData data) {
        // Merchant ID
        extractPattern("merchantId", text)
                .ifPresent(mid -> data.setMerchantId(mid.replace(" ", "").trim()));

        // Terminal ID and Terminal ID 00
        extractPattern("terminalId", text).ifPresent(tid -> {
            String cleanTid = tid.replace(" ", "").replace("\n", "").replace("\r", "").trim();
            data.setTerminalId(cleanTid);

            // Generate Terminal ID 00: replace digits "39" at position 3 and 4 with "00", otherwise copy TID
            if ( cleanTid.length() >= 4) {
                // String tid00 = cleanTid.replace("39", "00");
                if(cleanTid.substring(2, 4).equals("39")) {
                    String tid00 = cleanTid.substring(0, 2) + "00" + cleanTid.substring(4);
                    data.setTerminalId00(tid00);
                }
            } else {
                // If no "39" found, TID00 = TID
                data.setTerminalId00(cleanTid);
            }

            logger.debug("Generated TID00: {} from original TID: {}", data.getTerminalId00(), cleanTid);
        });

        // If no TID found at all, set both to empty string to avoid null
        if (data.getTerminalId() == null) {
            data.setTerminalId("");
            data.setTerminalId00("");
        }
    }

    private static java.util.Optional<String> extractPattern(String patternKey, String text) {
        Pattern pattern = PATTERNS.get(patternKey);
        if (pattern == null) {
            logger.error("Pattern not found for key: {}", patternKey);
            return java.util.Optional.empty();
        }

        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return java.util.Optional.of(matcher.group(1).trim());
        }
        return java.util.Optional.empty();
    }
}