package vn.quangkhongbiet.homestay_booking.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse {
    private Meta meta;
    private Object result;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Meta {
        private Integer page;
        private Integer pageSize;
        private Integer pages;
        private Long total;
    }
}
