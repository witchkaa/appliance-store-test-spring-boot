package com.epam.rd.autocode.assessment.appliances.config;


import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // ❗️ Отключаем автоматическое сопоставление вложенных свойств (implicit mapping)
        TypeMap<ApplianceRequestDto, Appliance> typeMap =
                modelMapper.createTypeMap(ApplianceRequestDto.class, Appliance.class, modelMapper.getConfiguration().copy().setImplicitMappingEnabled(false));

        // ❗️ Явно указываем, что manufacturer не мапим
        typeMap.addMappings(mapper -> {
            mapper.skip(Appliance::setId);
            mapper.skip(Appliance::setManufacturer);
        });
        typeMap.includeBase(ApplianceRequestDto.class, Appliance.class);
        typeMap.implicitMappings();
        return modelMapper;
    }
}
