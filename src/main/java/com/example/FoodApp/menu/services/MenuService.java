package com.example.FoodApp.menu.services;

import com.example.FoodApp.menu.dtos.MenuDTO;
import com.example.FoodApp.response.Response;

import java.util.List;

public interface MenuService {
    Response<MenuDTO> createMenu(MenuDTO menuDTO);
    Response<MenuDTO> updateMenu(MenuDTO menuDTO);
    Response<?> deleteMenu(Long id);
    Response<?> getMenuById(Long id);
    Response<List<MenuDTO>> getMenus(Long categoryId,String search);
}
