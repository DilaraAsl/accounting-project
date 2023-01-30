package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.exception.CategoryNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.CategoryService;
import com.cydeo.service.CompanyService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, MapperUtil mapperUtil, CompanyService companyService, ProductService productService) {
        this.categoryRepository = categoryRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
    }

    @Override
    public CategoryDto findById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("This category does not exist "));
        CategoryDto categoryDto = mapperUtil.convert(category, new CategoryDto());
        return categoryDto;
    }

    @Override
    public List<CategoryDto> listAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .filter(category -> category.getCompany().getTitle().equals(companyService.getCompanyDtoByLoggedInUser().getTitle()))
                .sorted(Comparator.comparing(Category::getDescription))
                .map(category -> {
                    CategoryDto dto = mapperUtil.convert(category, new CategoryDto());
                    return dto;
                })
                .collect(Collectors.toList());

    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        categoryDto.setCompany(companyService.getCompanyDtoByLoggedInUser());
        Category category = mapperUtil.convert(categoryDto, new Category());
        Category category1 = categoryRepository.save(category);

        return mapperUtil.convert(category1, new CategoryDto());
    }

    @Override
    public boolean isDescriptionExist(String description) {
        return categoryRepository.existsByDescriptionAndCompany_Title(description, companyService.getCompanyDtoByLoggedInUser().getTitle());
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {

        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new CategoryNotFoundException("This category does not exist "));
        category.setDescription(categoryDto.getDescription());
        Category category1 = categoryRepository.save(category);
        return mapperUtil.convert(category1, new CategoryDto());
    }

    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("This category does not exist "));
        category.setIsDeleted(true);
        category.setDescription(category.getDescription() + "-" + category.getId());
        categoryRepository.save(category);
    }


}
