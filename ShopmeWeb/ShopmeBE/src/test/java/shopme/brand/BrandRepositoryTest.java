package shopme.brand;

import com.example.shopmebe.ShopmeBeApplication;
import com.example.shopmebe.repository.BrandRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = ShopmeBeApplication.class)
@Rollback(false)
public class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("shopmedbTest")
            .withUsername("root")
            .withPassword("rootTest");


    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Test
    public void testCreateBrand1() {
        Category laptop = new Category(6);
        Brand acer = new Brand("Acer");
        acer.getCategories().add(laptop);

        Brand savedBrand = brandRepository.save(acer);

        assertThat(savedBrand.getId()).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateBrand2() {
        Category cellphones = new Category(4);
        Category tablets = new Category(6);

        Brand apple = new Brand("Apple");
        apple.getCategories().add(cellphones);
        apple.getCategories().add(tablets);

        Brand savedBrand = brandRepository.save(apple);

        assertThat(savedBrand.getId()).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateBrand3() {
        Category cellphones = new Category(4);
        Category tablets = new Category(6);

        Brand samsung = new Brand("Samsung");
        samsung.getCategories().add(cellphones);
        samsung.getCategories().add(tablets);

        Brand savedBrand = brandRepository.save(samsung);
        Optional<Brand> brandById = brandRepository.findById(1);


        assertThat(savedBrand.getId()).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
        assertThat(brandById).isPresent();
        assertThat(brandById.get().getId()).isPositive();
        assertThatCode(() -> assertThat(brandById.get()).usingRecursiveComparison().isEqualTo(samsung))
                .doesNotThrowAnyException();
    }

    @Test
    public void testFindAll() {
        Iterable<Brand> brands = brandRepository.findAll();
        brands.forEach(System.out::println);

        assertThat(brands).isEmpty();
    }

    @Test
    public void testGetById() {
        Brand brand = brandRepository.findById(1)
                .orElseThrow(() -> new NoSuchElementException("Brand with ID 1 not found"));

        assertNotNull(brand, "Brand with ID 1 not found");

        assertEquals("Acer", brand.getName(), "Brand name does not match expected value");
    }

    @Test
    public void testUpdateName() {
        String newName = "Samsung Electronics";
        Brand samsung = brandRepository.findById(3)
                .orElseThrow(() -> new NoSuchElementException("Brand with ID 3 not found"));
        samsung.setName(newName);

        Brand saveBrand = brandRepository.save(samsung);
        assertThat(saveBrand.getName()).isEqualTo(newName);
    }

    @Test
    public void testDelete() {
        Integer id = 2;
        brandRepository.deleteById(id);

        Optional<Brand> brand = brandRepository.findById(id);

        assertThat(brand.isPresent()).isFalse();
    }
}
