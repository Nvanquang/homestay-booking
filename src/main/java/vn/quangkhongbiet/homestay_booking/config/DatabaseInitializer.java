package vn.quangkhongbiet.homestay_booking.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quangkhongbiet.homestay_booking.domain.user.constant.Gender;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Permission;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.Role;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;
import vn.quangkhongbiet.homestay_booking.repository.PermissionRepository;
import vn.quangkhongbiet.homestay_booking.repository.RoleRepository;
import vn.quangkhongbiet.homestay_booking.repository.UserRepository;

@Service
@RequiredArgsConstructor 
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        System.out.println(">>> START INIT DATABASE");

        if (permissionRepository.count() == 0) {
            List<Permission> permissions = new ArrayList<>();

            // Amenity
            permissions.add(new Permission("Create amenity", "/amenities", "POST", "AMENITY"));
            permissions.add(new Permission("Get amenity by ID", "/amenities/{id}", "GET", "AMENITY"));
            permissions.add(new Permission("Get all amenities", "/amenities", "GET", "AMENITY"));
            permissions.add(new Permission("Delete amenity", "/amenities/{id}", "DELETE", "AMENITY"));

            // Booking
            permissions.add(new Permission("Create booking", "/bookings", "POST", "BOOKING"));
            permissions.add(new Permission("Get booking by ID", "/bookings/{id}", "GET", "BOOKING"));
            permissions.add(new Permission("Get all bookings", "/bookings", "GET", "BOOKING"));
            permissions.add(new Permission("Update booking", "/bookings/{id}", "PATCH", "BOOKING"));

            // Homestay
            permissions.add(new Permission("Create homestay", "/homestays", "POST", "HOMESTAY"));
            permissions.add(new Permission("Get homestay by ID", "/homestays/{id}", "GET", "HOMESTAY"));
            permissions.add(new Permission("Get all homestays", "/homestays", "GET", "HOMESTAY"));
            permissions.add(new Permission("Add amenities to homestay", "/homestays/{homestayId}/amenities", "POST", "HOMESTAY"));
            permissions.add(new Permission("Update homestay", "/homestays/{id}", "PATCH", "HOMESTAY"));
            permissions.add(new Permission("Delete homestay", "/homestays/{id}", "DELETE", "HOMESTAY"));

            // Homestay Image
            permissions.add(new Permission("Upload homestay images", "/homestay/{homestayId}/images", "POST", "HOMESTAY_IMAGE"));
            permissions.add(new Permission("Get homestay images by homestayId", "/homestay/{homestayId}/images", "GET", "HOMESTAY_IMAGE"));
            permissions.add(new Permission("Delete homestay image", "/homestay-images/{id}", "DELETE", "HOMESTAY_IMAGE"));

            // Availability
            permissions.add(new Permission("Create homestay availability", "/availabilities", "POST", "AVAILABILITY"));

            // Location
            permissions.add(new Permission("Get location by ID", "/locations/{id}", "GET", "LOCATION"));

            // Permission
            permissions.add(new Permission("Create permission", "/permissions", "POST", "PERMISSION"));
            permissions.add(new Permission("Get permission by ID", "/permissions/{id}", "GET", "PERMISSION"));
            permissions.add(new Permission("Get all permissions", "/permissions", "GET", "PERMISSION"));
            permissions.add(new Permission("Update permission", "/permissions/{id}", "PATCH", "PERMISSION"));
            permissions.add(new Permission("Delete permission", "/permissions/{id}", "DELETE", "PERMISSION"));

            // Role
            permissions.add(new Permission("Create role", "/roles", "POST", "ROLE"));
            permissions.add(new Permission("Get role by ID", "/roles/{id}", "GET", "ROLE"));
            permissions.add(new Permission("Get all roles", "/roles", "GET", "ROLE"));
            permissions.add(new Permission("Update role", "/roles/{id}", "PATCH", "ROLE"));
            permissions.add(new Permission("Delete role", "/roles/{id}", "DELETE", "ROLE"));

            // User
            permissions.add(new Permission("Create user", "/users", "POST", "USER"));
            permissions.add(new Permission("Get user by ID", "/users/{id}", "GET", "USER"));
            permissions.add(new Permission("Get all users", "/users", "GET", "USER"));
            permissions.add(new Permission("Update user", "/users/{id}", "PATCH", "USER"));
            permissions.add(new Permission("Delete user", "/users/{id}", "DELETE", "USER"));

            permissionRepository.saveAll(permissions);
        }

        if (roleRepository.count() == 0) {
            Role admin = new Role();
            admin.setName("SUPER_ADMIN");
            admin.setDescription("Toàn quyền hệ thống");
            admin.setActive(true);
            admin.setPermissions(permissionRepository.findAll());
            roleRepository.save(admin);
        }

        if (userRepository.count() == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("12345678"));
            adminUser.setUserName("Super Admin");
            adminUser.setGender(Gender.MALE);
            adminUser.setPhoneNumber("+84123456789");
            adminUser.setCreatedAt(java.time.Instant.now());
            adminUser.setCreatedBy("SYSTEM");

            Role adminRole = roleRepository.findByName("SUPER_ADMIN").get();
            adminUser.setRole(adminRole);

            userRepository.save(adminUser);
        }

        System.out.println(">>> END INIT DATABASE");
    }
}
