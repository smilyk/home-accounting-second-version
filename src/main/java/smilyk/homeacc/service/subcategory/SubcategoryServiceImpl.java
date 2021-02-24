package smilyk.homeacc.service.subcategory;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smilyk.homeacc.constants.CategorySubcategoryConstant;
import smilyk.homeacc.dto.SubcategoryDto;
import smilyk.homeacc.model.Subcategory;
import smilyk.homeacc.repo.SubcategoryRepository;
import smilyk.homeacc.service.user.UserServiceImpl;
import smilyk.homeacc.utils.Utils;

@Service
public class SubcategoryServiceImpl implements SubcategoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    ModelMapper modelMapper = new ModelMapper();
    @Autowired
    SubcategoryRepository subcategoryRepository;
    @Autowired
    Utils utils;
    @Override
    public Subcategory save(SubcategoryDto subcategoryDto) {
        Subcategory subcategory = modelMapper.map(subcategoryDto, Subcategory.class);
        subcategory.setSubcategoryUuid(utils.generateUserUuid().toString());
        subcategory.setDeleted(false);
        Subcategory savedSubcategory = subcategoryRepository.save(subcategory);
        LOGGER.info(CategorySubcategoryConstant.SUBCATEGORY_WITH_NAME + subcategoryDto.getSubCategoryName() +
            CategorySubcategoryConstant.CREATED);
        return savedSubcategory;
    }
}