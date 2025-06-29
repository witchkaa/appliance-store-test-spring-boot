package com.epam.rd.autocode.assessment.appliances.config;


import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderRowDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrdersDto;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<ApplianceRequestDto, Appliance> typeMap =
                modelMapper.createTypeMap(ApplianceRequestDto.class, Appliance.class, modelMapper.getConfiguration().copy().setImplicitMappingEnabled(false));

        typeMap.addMappings(mapper -> {
            mapper.skip(Appliance::setId);
            mapper.skip(Appliance::setManufacturer);
        });
        typeMap.includeBase(ApplianceRequestDto.class, Appliance.class);
        typeMap.implicitMappings();
        modelMapper.createTypeMap(Orders.class, OrdersDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getClient().getId(), OrdersDto::setClientId);
            mapper.map(src -> src.getClient().getName(), OrdersDto::setClientName);
        });
        modelMapper.createTypeMap(OrderRow.class, OrderRowDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getAppliance().getId(), OrderRowDto::setApplianceId);
            mapper.map(src -> src.getAppliance().getName(), OrderRowDto::setApplianceName);
        });
        return modelMapper;
    }
}
