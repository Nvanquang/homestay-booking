package vn.quangkhongbiet.homestay_booking.domain.homestay.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "homestay_images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomestayImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Homestay là bắt buộc")
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "homestay_id")
    private Homestay homestay;

    @NotBlank(message = "URL ảnh là bắt buộc")
    @Size(max = 255, message = "URL ảnh không được vượt quá 255 ký tự")
    private String imageUrl;

    private String publicId;
}
