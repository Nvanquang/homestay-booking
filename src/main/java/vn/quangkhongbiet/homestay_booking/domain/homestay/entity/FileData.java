package vn.quangkhongbiet.homestay_booking.domain.homestay.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileData {
    private String originalFilename;
    private String contentType;
    private long size;
    private byte[] bytes;
}
