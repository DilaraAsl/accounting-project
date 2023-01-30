package com.cydeo.service.impl.unitTest;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Address;
import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Company;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.impl.ClientVendorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cydeo.TestDocumentInitializer.getClientVendor;
import static com.cydeo.TestDocumentInitializer.getCompany;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientVendorServiceImplTest {
    @Mock
    ClientVendorRepository clientVendorRepository;
    @Mock
    CompanyService companyService;

    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    @InjectMocks
    ClientVendorServiceImpl clientVendorService;
    static ClientVendor clientVendor;

    @BeforeEach
    void setUp() {
        clientVendor = new ClientVendor();
        clientVendor.setId(1L);
        clientVendor.setClientVendorType(ClientVendorType.VENDOR);
        clientVendor.setClientVendorName("Test_ClientVendor");
        clientVendor.setAddress(new Address());
        clientVendor.setWebsite("https://www.test.com");
        clientVendor.setPhone("+1 (111) 111-1111");

    }

    @Test
    void listCompanyVendors() {
        List<ClientVendor> list = getClientVendors().stream()
                .filter(clientVendor -> clientVendor.getCompany().getTitle().equals(getCompany(CompanyStatus.ACTIVE).getTitle()))
                .sorted(Comparator.comparing(ClientVendor::getClientVendorType).reversed()
                        .thenComparing(ClientVendor::getClientVendorName))
                .collect(Collectors.toList());


        List<ClientVendor> expectedList = list.stream()
                .filter(clientVendor1 -> clientVendor1.getClientVendorType().equals(ClientVendorType.VENDOR))
                .collect(Collectors.toList());

        when(clientVendorRepository.findAll()).thenReturn(expectedList);
        lenient().when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(getCompany(CompanyStatus.ACTIVE));

        List<ClientVendorDto> actualList = clientVendorService.listCompanyVendors();

        assertEquals(1, actualList.size());
        assertEquals(expectedList.get(0).getClientVendorName(), actualList.get(0).getClientVendorName());
        assertEquals("Vendor", actualList.get(0).getClientVendorType().getValue());

    }

    @Test
    void listAllClientVendors() {

        List<ClientVendor> expectedList = getClientVendors().stream()
                .filter(clientVendor -> clientVendor.getCompany().getTitle().equals(getCompany(CompanyStatus.ACTIVE).getTitle()))
                .sorted(Comparator.comparing(ClientVendor::getClientVendorType).reversed()
                        .thenComparing(ClientVendor::getClientVendorName))
                .collect(Collectors.toList());

        when(clientVendorRepository.findAll()).thenReturn(expectedList);

        lenient().when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(getCompany(CompanyStatus.ACTIVE));


        List<ClientVendorDto> actualList = clientVendorService.listAllClientVendors();


        assertEquals(3, actualList.size());
        assertEquals(expectedList.get(0).getClientVendorName(), actualList.get(0).getClientVendorName());
        assertEquals(expectedList.get(1).getClientVendorName(), actualList.get(1).getClientVendorName());
        assertEquals("Client", actualList.get(0).getClientVendorType().getValue());
        assertEquals("Client", actualList.get(1).getClientVendorType().getValue());
        assertEquals("Vendor", actualList.get(2).getClientVendorType().getValue());

    }

    @Test
    void findById() {
        Long id = 1L;
        when(clientVendorRepository.findById(id)).thenReturn(Optional.of(clientVendor));

        ClientVendorDto dto = clientVendorService.findById(id);

        verify(clientVendorRepository).findById(id);
        assertEquals(1L, dto.getId());
    }

    @Test
    void save_happyPath() {
        when(clientVendorRepository.save(any())).thenReturn(clientVendor);
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(getCompany(CompanyStatus.ACTIVE));

        ClientVendorDto dto = clientVendorService.save(getClientVendor(ClientVendorType.VENDOR));

        verify(mapperUtil).convert(any(ClientVendor.class), any(ClientVendorDto.class));
        assertThat(dto).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(dto);

    }

    @Test
    void save_throws_exception() {

        Throwable throwable = assertThrows(IllegalArgumentException.class, () -> clientVendorService.save(null));

        assertEquals("source cannot be null", throwable.getMessage());
    }

    @Test
    void listCompanyClients() {

        List<ClientVendor> list = getClientVendors().stream()
                .filter(clientVendor -> clientVendor.getCompany().getTitle().equals(getCompany(CompanyStatus.ACTIVE).getTitle()))
                .sorted(Comparator.comparing(ClientVendor::getClientVendorType).reversed()
                        .thenComparing(ClientVendor::getClientVendorName))
                .collect(Collectors.toList());


        List<ClientVendor> expectedList = list.stream()
                .filter(clientVendor1 -> clientVendor1.getClientVendorType().equals(ClientVendorType.CLIENT))
                .collect(Collectors.toList());

        when(clientVendorRepository.findAll()).thenReturn(list);

        lenient().when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(getCompany(CompanyStatus.ACTIVE));


        List<ClientVendorDto> actualList = clientVendorService.listCompanyClients();

        assertEquals(2, actualList.size());
        assertEquals(expectedList.get(0).getClientVendorName(), actualList.get(0).getClientVendorName());
        assertEquals(expectedList.get(1).getClientVendorName(), actualList.get(1).getClientVendorName());


    }

    @Test
    void deleteClientVendorById() {
        Long id = 1L;
        ClientVendor clientVendor1 = new ClientVendor();
        clientVendor1.setId(id);
        clientVendor1.setIsDeleted(false);

        clientVendor1.setClientVendorName("test-client-vendor");
        when(clientVendorRepository.findById(id)).thenReturn(Optional.of(clientVendor1));

        clientVendorService.deleteClientVendorById(id);

        verify(clientVendorRepository).save(clientVendor1);

        assertTrue(clientVendor1.getIsDeleted());
        assertEquals("test-client-vendor-1", clientVendor1.getClientVendorName());

    }

    @Test
    void update() {

        clientVendor.setCompany(mapperUtil.convert(getCompany(CompanyStatus.ACTIVE), new Company()));

        when(clientVendorRepository.findById(anyLong())).thenReturn(Optional.ofNullable(clientVendor));

        ClientVendor clientVendor1 = clientVendor;
        clientVendor1.setClientVendorName("update-test");
        ClientVendorDto clientVendorDto = mapperUtil.convert(clientVendor1, new ClientVendorDto());
        when(clientVendorRepository.save(any())).thenReturn(clientVendor);

        ClientVendorDto actual = clientVendorService.update(clientVendorDto);

        assertEquals(clientVendor1.getClientVendorName(), clientVendorDto.getClientVendorName());


    }

    private List<ClientVendor> getClientVendors() {
        ClientVendorDto dto = getClientVendor(ClientVendorType.VENDOR);
        dto.setCompany(getCompany(CompanyStatus.ACTIVE));
        dto.setClientVendorName("Test_ClientVendor");
        ClientVendor clientVendor = mapperUtil.convert(dto, new ClientVendor());

        ClientVendorDto dto1 = getClientVendor(ClientVendorType.CLIENT);
        dto1.setCompany(getCompany(CompanyStatus.ACTIVE));
        dto1.setClientVendorName("Test2_ClientVendor");
        ClientVendor clientVendor2 = mapperUtil.convert(dto1, new ClientVendor());

        ClientVendorDto dto2 = getClientVendor(ClientVendorType.CLIENT);
        CompanyDto companyDto = getCompany(CompanyStatus.ACTIVE);
        dto2.setCompany(companyDto);
        dto2.setClientVendorName("Test3_ClientVendor");
        ClientVendor clientVendor3 = mapperUtil.convert(dto2, new ClientVendor());
        return Arrays.asList(clientVendor, clientVendor2, clientVendor3);
    }

}