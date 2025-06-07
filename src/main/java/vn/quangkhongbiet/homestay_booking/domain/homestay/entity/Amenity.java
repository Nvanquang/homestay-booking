package vn.quangkhongbiet.homestay_booking.domain.homestay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "amenities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên tiện ích là bắt buộc")
    @Size(min = 2, max = 50, message = "Tên tiện ích phải có độ dài từ 2 đến 50 ký tự")
    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "amenities", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Homestay> homestays;
}
