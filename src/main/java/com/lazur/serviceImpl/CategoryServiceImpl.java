package com.lazur.serviceImpl;

import com.lazur.entities.Category;
import com.lazur.entities.CategoryCode;
import com.lazur.entities.Model;
import com.lazur.exeptions.CategoryNotFoundExeption;
import com.lazur.exeptions.ModelNotFoundExeption;
import com.lazur.models.categories.*;
import com.lazur.models.models.ModelEditModel;
import com.lazur.repositories.CategoryRepository;
import com.lazur.services.CategoryCodeService;
import com.lazur.services.CategoryService;
import com.lazur.services.ModelService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Service
public class CategoryServiceImpl implements CategoryService {
    //to avoid cilcular bean dependency
    @Autowired
    private CategoryCodeService categoryCodeService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelService modelService;


    @Override
    public List<CategoryViewModel> getCategories() {
        List<Category> categoryList = this.categoryRepository.findAllCategories();
        List<CategoryViewModel> categoryViewModels = new ArrayList<>();
        for (Category category : categoryList) {
            CategoryViewModel categoryViewModel = this.modelMapper.map(category, CategoryViewModel.class);
            String code = getCategoryCodes(category);
            categoryViewModel.setCode(code);
            categoryViewModels.add(categoryViewModel);
        }

        return categoryViewModels;
    }

    @Override
    @Transactional
    public void save(CategoryBindingModel categoryBindingModel) {
        CategoryCode categoryCode = new CategoryCode();
        categoryCode.setCode(categoryBindingModel.getCode());
        Category category = this.modelMapper.map(categoryBindingModel, Category.class);
        category.addCategoryCodes(categoryCode);

        this.categoryRepository.save(category);
    }

    @Override
    public Category findCategory(String category) {
        Category foundCategory = this.categoryRepository.findByName(category);

        return foundCategory;
    }

    @Override
    @Transactional
    public ModelEditModel findByModel(Long id) {
        Category category = this.categoryRepository.findCategoryByModelId(id);
        if (category == null) {
            throw new CategoryNotFoundExeption();
        }

        ModelEditModel modelEditModel = new ModelEditModel();
        Model model = category.getModels().stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
        if (model == null) {
            throw new ModelNotFoundExeption();
        }

        modelEditModel.setModelId(model.getId());
        modelEditModel.setModelName(model.getName());
        modelEditModel.setModelCode(model.getCode().substring(1));
        modelEditModel.setCategoryCode(String.valueOf(model.getCode().charAt(0)));
        for (CategoryCode categoryCode : category.getCategoryCodes()) {
            modelEditModel.addCode(categoryCode.getCode());
        }

        return modelEditModel;
    }

    @Override
    public CategoryViewModel findAllModelsByCategory(String name) {
        Category category = this.categoryRepository.findByName(name);
        CategoryViewModel categoryViewModel = this.modelMapper.map(category, CategoryViewModel.class);
        String code = getCategoryCodes(category);
        categoryViewModel.setCode(code);
        return categoryViewModel;
    }

    @Override
    public CategoryEditViewModel findCategoryByName(String categoryName) {
        Category category = this.categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new CategoryNotFoundExeption();
        }

        CategoryEditViewModel categoryEditViewModel = this.modelMapper.map(category, CategoryEditViewModel.class);
        for (CategoryCode categoryCode : category.getCategoryCodes()) {
            categoryEditViewModel.addCode(categoryCode.getCode());
        }

        return categoryEditViewModel;
    }

    @Override
    public void update(Long categoryId, CategoryAndModelUpdateModel categoryUpdateModel) {
        Category category = this.categoryRepository.findOne(categoryId);
        if (category == null) {
            throw new CategoryNotFoundExeption();
        }

        CategoryCode categoryCode = null;
        if (categoryUpdateModel.getOldCode() == null) {
            categoryCode = new CategoryCode();
            categoryCode.setCode(categoryUpdateModel.getCode());
            category.addCategoryCodes(categoryCode);
        } else {
            categoryCode = this.categoryCodeService.getCode(categoryId, categoryUpdateModel.getOldCode());
            category.setName(categoryUpdateModel.getName());
            categoryCode.setCode(categoryUpdateModel.getCode());
            this.modelService.updateCodes(category.getName(), categoryUpdateModel.getOldCode(), categoryUpdateModel.getCode());
        }

        this.categoryCodeService.update(categoryCode);
    }

    @Override
    public CategoryEditModel findDeleteCategoryByName(String categoryName) {
        Category category = this.categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new CategoryNotFoundExeption();
        }

        CategoryEditModel categoryEditModel = this.modelMapper.map(category, CategoryEditModel.class);
        categoryEditModel.setCode(category.getCategoryCodes().stream().findFirst().map(e -> e.getCode()).get());
        return categoryEditModel;
    }

    @Override
    public void delete(Long id) {
        this.categoryRepository.delete(id);
    }


    private String getCategoryCodes(Category category) {
        StringJoiner stringJoiner = new StringJoiner(",");
        for (CategoryCode categoryCode : category.getCategoryCodes()) {
            stringJoiner.add(categoryCode.getCode());
        }

        return stringJoiner.toString();
    }
}
