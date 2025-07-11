package vn.quangkhongbiet.homestay_booking.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.SearchHomestayResponse;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Homestay;

@Repository
public interface HomestayRepository extends JpaRepository<Homestay, Long>, JpaSpecificationExecutor<Homestay> {
        boolean existsById(Long id);

        List<Homestay> findByIdIn(List<Long> ids);

        @Modifying
        @Query(value = """
                        UPDATE homestays
                        SET geom = ST_Transform(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), 3857)
                        WHERE id = :id
                        """, nativeQuery = true)
        void updateGeom(@Param("id") Long id,
                        @Param("longitude") Double longitude,
                        @Param("latitude") Double latitude);

        // @Query(value = """
        // WITH destination AS (
        // SELECT ST_Transform(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326),
        // 3857) AS geom
        // )
        // SELECT id, name, description, phone_number, status, guests, vh.night_amount,
        // vh.total_amount, address, longitude, latitude
        // FROM destination d, homestays hs INNER JOIN
        // (
        // SELECT h.id, AVG(ha.price) AS night_amount, SUM(ha.price) AS total_amount
        // FROM destination d,
        // homestays h
        // JOIN homestay_availability ha ON h.id = ha.homestay_id
        // WHERE ST_DWithin(h.geom, d.geom, :radius)
        // AND h.guests >= :guests
        // AND ha.date BETWEEN :checkinDate AND :checkoutDate
        // AND ha.status = :status
        // GROUP BY h.id
        // HAVING COUNT(ha.date) = :nights
        // ) AS vh USING (id)
        // ORDER BY hs.geom <-> d.geom
        // """, nativeQuery = true)
        // List<ResSearchHomestayDTO> searchHomestay(@Param("longitude") Double
        // longitude,
        // @Param("latitude") Double latitude,
        // @Param("radius") Double radius,
        // @Param("checkinDate") LocalDate checkinDate,
        // @Param("checkoutDate") LocalDate checkoutDate,
        // @Param("nights") Integer nights,
        // @Param("guests") Integer guests,
        // @Param("status") String status);

        @Query(value = """
                WITH destination AS (
                        SELECT ST_Transform(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), 3857) AS geom
                ),
                homestay_base AS (
                        SELECT
                        hs.id AS id,
                        hs.name AS name,
                        hs.description AS description,
                        hs.phone_number AS phoneNumber,
                        hs.status AS status,
                        hs.guests AS guests,
                        vh.night_amount AS nightAmount,
                        vh.total_amount AS totalAmount,
                        hs.address AS address,
                        hs.longitude AS longitude,
                        hs.latitude AS latitude
                        FROM destination d
                        INNER JOIN homestays hs ON ST_DWithin(hs.geom, d.geom, :radius)
                        INNER JOIN (
                        SELECT h.id, AVG(ha.price) AS night_amount, SUM(ha.price) AS total_amount
                        FROM destination d
                        INNER JOIN homestays h ON ST_DWithin(h.geom, d.geom, :radius)
                        JOIN homestay_availability ha ON h.id = ha.homestay_id
                        WHERE h.guests >= :guests
                                AND ha.date BETWEEN :checkinDate AND :checkoutDate
                                AND ha.status = :status
                        GROUP BY h.id
                        HAVING COUNT(ha.date) = :nights
                        ) AS vh ON hs.id = vh.id
                ),
                homestay_with_images AS (
                        SELECT
                        hb.*,
                        array_agg(DISTINCT hi.image_url) AS images
                        FROM homestay_base hb
                        LEFT JOIN homestay_images hi ON hb.id = hi.homestay_id
                        GROUP BY hb.id, hb.name, hb.description, hb.phoneNumber, hb.status, hb.guests,
                                hb.nightAmount, hb.totalAmount, hb.address, hb.longitude, hb.latitude
                ),
                homestay_with_amenities AS (
                        SELECT
                        hwi.*,
                        array_agg(DISTINCT ha.amenity_id) AS amenities
                        FROM homestay_with_images hwi
                        LEFT JOIN homestay_amenity ha ON hwi.id = ha.homestay_id
                        GROUP BY hwi.id, hwi.name, hwi.description, hwi.phoneNumber, hwi.status, hwi.guests,
                                hwi.nightAmount, hwi.totalAmount, hwi.address, hwi.longitude, hwi.latitude, hwi.images
                )
                SELECT * FROM homestay_with_amenities
                ORDER BY ST_Distance(
                        (SELECT geom FROM destination),
                        ST_Transform(ST_SetSRID(ST_MakePoint(homestay_with_amenities.longitude, homestay_with_amenities.latitude), 4326), 3857)
                )
                """, nativeQuery = true)
        List<SearchHomestayResponse> searchHomestay(@Param("longitude") Double longitude,
                        @Param("latitude") Double latitude,
                        @Param("radius") Double radius,
                        @Param("checkinDate") LocalDate checkinDate,
                        @Param("checkoutDate") LocalDate checkoutDate,
                        @Param("nights") Integer nights,
                        @Param("guests") Integer guests,
                        @Param("status") String status);

}
