package com.restaurant.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.restaurant.model.MenuItem;
import com.restaurant.model.Role;
import com.restaurant.model.User;
import com.restaurant.repository.MenuItemRepository;
import com.restaurant.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, MenuItemRepository menuItemRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed admin user
        if (!userRepository.existsByEmail("admin@restaurant.com")) {
            User admin = new User();
            admin.setUsername("Admin");
            admin.setEmail("admin@restaurant.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        // Seed sample menu items
        if (menuItemRepository.count() == 0) {
            menuItemRepository.save(createItem("Bruschetta", "Starters", "Toasted bread topped with tomatoes, garlic, and fresh basil", 7.99));
            menuItemRepository.save(createItem("Caesar Salad", "Starters", "Crisp romaine lettuce with Caesar dressing and croutons", 9.99));
            menuItemRepository.save(createItem("Calamari", "Starters", "Lightly fried squid rings served with marinara sauce", 11.99));
            menuItemRepository.save(createItem("Grilled Salmon", "Main Course", "Fresh Atlantic salmon with lemon butter and seasonal vegetables", 24.99));
            menuItemRepository.save(createItem("Beef Tenderloin", "Main Course", "8oz filet mignon with red wine reduction and truffle mash", 34.99));
            menuItemRepository.save(createItem("Mushroom Risotto", "Main Course", "Creamy arborio rice with wild mushrooms and parmesan", 18.99));
            menuItemRepository.save(createItem("Tiramisu", "Desserts", "Classic Italian dessert with espresso-soaked ladyfingers", 8.99));
            menuItemRepository.save(createItem("Crème Brûlée", "Desserts", "Rich vanilla custard with a caramelized sugar crust", 7.99));
            menuItemRepository.save(createItem("Sparkling Water", "Drinks", "Still or sparkling mineral water", 3.99));
            menuItemRepository.save(createItem("Fresh Lemonade", "Drinks", "Freshly squeezed lemonade with mint", 5.99));
        }
    }

    private MenuItem createItem(String name, String category, String description, double price) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setCategory(category);
        item.setDescription(description);
        item.setPrice(price);
        item.setAvailable(true);
        return item;
    }
}