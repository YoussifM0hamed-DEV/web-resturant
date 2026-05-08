package com.restaurant.service;

import com.restaurant.model.MenuItem;
import com.restaurant.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuService() {
        this.menuItemRepository = null;
    }

    public List<MenuItem> getAll() {
        return menuItemRepository.findAll();
    }

    public Map<String, List<MenuItem>> getMenuGroupedByCategory() {
        return menuItemRepository.findByAvailableTrue()
            .stream()
            .collect(Collectors.groupingBy(MenuItem::getCategory));
    }

    public MenuItem getById(Long id) {
        return menuItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public MenuItem save(MenuItem item) {
        return menuItemRepository.save(item);
    }

    public void delete(Long id) {
        menuItemRepository.deleteById(id);
    }

    public MenuItem toggleAvailability(Long id) {
        MenuItem item = getById(id);
        item.setAvailable(!item.isAvailable());
        return menuItemRepository.save(item);
    }

    public MenuItemRepository getMenuItemRepository() {
        return menuItemRepository;
    }
}