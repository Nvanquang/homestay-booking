package vn.quangkhongbiet.homestay_booking.domain.homestay.entity.address;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "locations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên tỉnh/thành phố là bắt buộc")
    @Size(max = 100, message = "Tên tỉnh/thành phố không được vượt quá 100 ký tự")
    private String city;

    @NotBlank(message = "Tên quận/huyện là bắt buộc")
    @Size(max = 100, message = "Tên quận/huyện không được vượt quá 100 ký tự")
    private String district;

    @Size(max = 100, message = "Tên phường/xã không được vượt quá 100 ký tự")
    private String ward;

    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Homestay> homestays;
}