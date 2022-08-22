package com.sigma.oilstation.serviceImpl;

import com.sigma.oilstation.entity.Fuel;
import com.sigma.oilstation.entity.FuelReport;
import com.sigma.oilstation.entity.User;
import com.sigma.oilstation.exceptions.PageSizeException;
import com.sigma.oilstation.mapper.FuelReportMapper;
import com.sigma.oilstation.payload.ApiResponse;
import com.sigma.oilstation.payload.FuelReportDto;
import com.sigma.oilstation.payload.FuelReportPostDto;
import com.sigma.oilstation.payload.FuelReportTotalDto;
import com.sigma.oilstation.repository.BranchRepository;
import com.sigma.oilstation.repository.FuelReportRepository;
import com.sigma.oilstation.repository.FuelRepository;
import com.sigma.oilstation.repository.UserRepository;
import com.sigma.oilstation.service.FuelReportService;
import com.sigma.oilstation.utils.CommandUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuelReportServiceImpl implements FuelReportService {
    private final FuelReportRepository fuelReportRepository;
    private final FuelRepository fuelRepository;
    private final UserRepository userRepository;
    private final FuelReportMapper mapper;
    private final BranchRepository branchRepository;


    @Override
    public ApiResponse<?> create(FuelReportPostDto fuelPostDto) {
        Optional<User> optionalUser = userRepository.findById(fuelPostDto.getEmployeeId());
        Optional<Fuel> optionalFuel = fuelRepository.findById(fuelPostDto.getFuelId());

        if (optionalUser.isEmpty()) {
            return new ApiResponse<>(false, "Ishchi mavjud emas!");
        }
        if (optionalFuel.isEmpty()) {
            return new ApiResponse<>(false, "Yoqilg'i mavjud emas!");
        }
        FuelReport fuelReport = mapper.toEntity(fuelPostDto);
        fuelReport.setFuel(optionalFuel.get());
        fuelReport.setEmployee(optionalUser.get());
        fuelReportRepository.save(fuelReport);

        FuelReport oldFuelReport = fuelReportRepository.findByActiveShiftTrueAndEmployeeBranchId(optionalUser.get().getBranch().getId());
        oldFuelReport.setActiveShift(false);
        oldFuelReport.setAmountAtEndOfShift(fuelReport.getAmountAtEndOfShift());
        return new ApiResponse<>(true, "Hisobot saqlandi!");
    }

    @Override
    public ApiResponse<?> edit(FuelReportDto fuelReportDto) {
        Optional<FuelReport> optionalFuelReport = fuelReportRepository.findById(fuelReportDto.getId());
        Optional<User> optionalEmployee = userRepository.findById(fuelReportDto.getEmployeeId());
        Optional<Fuel> optionalFuel = fuelRepository.findById(fuelReportDto.getFuelId());


        if (optionalFuelReport.isEmpty())
            return new ApiResponse<>(false, "Hisobot mavjud emas!");
        if (optionalEmployee.isEmpty())
            return new ApiResponse<>(false, "Ishchi mavjud emas!");
        if (optionalFuel.isEmpty())
            return new ApiResponse<>(false, "Yoqilg'i mavjud emas!");


        FuelReport fuelReport = mapper.toEntity(fuelReportDto);
        fuelReport.setFuel(optionalFuel.get());
        fuelReport.setEmployee(optionalEmployee.get());
        fuelReportRepository.save(fuelReport);
        return new ApiResponse<>(true, "Hisobot saqlandi!");
    }

    @Override
    public ApiResponse<?> getById(UUID id) {
        Optional<FuelReport> optionalFuelReport = fuelReportRepository.findById(id);
        if (optionalFuelReport.isEmpty())
            return new ApiResponse<>(false, "Hisobot mavjud emas!");
        return new ApiResponse<>(true, "Hisobot!", mapper.toDto(optionalFuelReport.get()));
    }


    @Override
    public ApiResponse<?> get() {
        List<FuelReport> fuelReports = fuelReportRepository.findAll();
        return new ApiResponse<>(true, "Hamma hisobotlar!", getTotalFuelReport(fuelReports));
    }


    @Override
    public ApiResponse<?> get(int page, int size) {
        try {
            Page<FuelReport> fuelReportPage = fuelReportRepository.findAll(CommandUtils.simplePageable(page, size));
            HashMap<String, Object> response = new HashMap<>();
            response.put("fuelReports", getTotalFuelReport(fuelReportPage));
            response.put("currentPage", fuelReportPage.getNumber());
            response.put("totalItems", fuelReportPage.getTotalElements());
            response.put("totalPages", fuelReportPage.getTotalElements());
            return new ApiResponse<>(true, "Hisobotlar page!", response);
        } catch (PageSizeException e) {
            return new ApiResponse<>(false, e.getMessage());
        }
    }

    @Override
    public ApiResponse<?> getByBranchId(UUID branchId) {
        if(!branchRepository.existsById(branchId))
            return new ApiResponse<>(false,"Filial mavjud emas!");
        List<FuelReport> fuelReportList = fuelReportRepository.findAllByEmployeeBranchId(branchId);
        return new ApiResponse<>(true,"Filial hisobotlari!",getTotalFuelReport(fuelReportList));
    }

    @Override
    public ApiResponse<?> getDailyFuelReport(Integer page, Integer size) {
        Timestamp today = Timestamp.valueOf(LocalDateTime.now());
        Timestamp yesterday = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
        try {
            Page<FuelReport> fuelReportPage = fuelReportRepository.findAllByReportTimeIsBetween(today, yesterday, CommandUtils.simplePageable(page, size));
            Map<String, Object> response = new HashMap<>();
            response.put("fuelReports", getTotalFuelReport(fuelReportPage));
            response.put("currentPage", fuelReportPage.getNumber());
            response.put("totalItems", fuelReportPage.getTotalElements());
            response.put("totalPages", fuelReportPage.getTotalPages());
            return new ApiResponse<>(true, "Kunlik hisobotlar!", response);
        } catch (PageSizeException e) {
            return new ApiResponse<>(false, e.getMessage());
        }
    }

    @Override
    public ApiResponse<?> getWeeklyFuelReport(Integer page, Integer size) {
        Timestamp today = Timestamp.valueOf(LocalDateTime.now());
        Timestamp aWeekAgo = Timestamp.valueOf(LocalDateTime.now().minusWeeks(1));
        try {
            Page<FuelReport> fuelReportPage = fuelReportRepository.findAllByReportTimeIsBetween(today, aWeekAgo, CommandUtils.simplePageable(page, size));
            Map<String, Object> response = new HashMap<>();
            response.put("fuelReports", getTotalFuelReport(fuelReportPage));
            response.put("currentPage", fuelReportPage.getNumber());
            response.put("totalItems", fuelReportPage.getTotalElements());
            response.put("totalPages", fuelReportPage.getTotalPages());
            return new ApiResponse<>(true, "Haftalik hisobotlar!", response);
        } catch (PageSizeException e) {
            return new ApiResponse<>(false, e.getMessage());
        }
    }

    @Override
    public ApiResponse<?> getMonthlyFuelReport(Integer page, Integer size) {
        Timestamp today = Timestamp.valueOf(LocalDateTime.now());
        Timestamp aMonthAgo = Timestamp.valueOf(LocalDateTime.now().minusMonths(1));
        try {
            Page<FuelReport> fuelReportPage = fuelReportRepository.findAllByReportTimeIsBetween(today, aMonthAgo, CommandUtils.simplePageable(page, size));
            Map<String, Object> response = new HashMap<>();
            response.put("fuelReports", getTotalFuelReport(fuelReportPage));
            response.put("currentPage", fuelReportPage.getNumber());
            response.put("totalItems", fuelReportPage.getTotalElements());
            response.put("totalPages", fuelReportPage.getTotalPages());
            return new ApiResponse<>(true, "Oylik hisobotlar!", response);
        } catch (PageSizeException e) {
            return new ApiResponse<>(false, e.getMessage());
        }
    }

    @Override
    public ApiResponse<?> getAnnuallyFuelReport(Integer page, Integer size) {
        Timestamp today = Timestamp.valueOf(LocalDateTime.now());
        Timestamp aYearAgo = Timestamp.valueOf(LocalDateTime.now().minusYears(1));
        try {
            Page<FuelReport> fuelReportPage = fuelReportRepository.findAllByReportTimeIsBetween(today, aYearAgo, CommandUtils.simplePageable(page, size));
            Map<String, Object> response = new HashMap<>();
            response.put("fuelReports", getTotalFuelReport(fuelReportPage));
            response.put("currentPage", fuelReportPage.getNumber());
            response.put("totalItems", fuelReportPage.getTotalElements());
            response.put("totalPages", fuelReportPage.getTotalPages());
            return new ApiResponse<>(true, "Yillik hisobotlar!", response);
        } catch (PageSizeException e) {
            return new ApiResponse<>(false, e.getMessage());
        }
    }

    @Override
    public ApiResponse<?> getInterimFuelReport(Integer page, Integer size,Date startDate,Date endDate) {
        try {
            Page<FuelReport> fuelReportPage = fuelReportRepository.findAllByReportTimeIsBetween(new Timestamp(startDate.getTime()),new Timestamp(endDate.getTime()), CommandUtils.simplePageable(page, size));
            Map<String, Object> response = new HashMap<>();
            response.put("fuelReports", getTotalFuelReport(fuelReportPage));
            response.put("currentPage", fuelReportPage.getNumber());
            response.put("totalItems", fuelReportPage.getTotalElements());
            response.put("totalPages", fuelReportPage.getTotalPages());
            return new ApiResponse<>(true, "Vaqt oraligidagi hisobotlar!", response);
        } catch (PageSizeException e) {
            return new ApiResponse<>(false, e.getMessage());
        }
    }

    private FuelReportTotalDto getTotalFuelReport(Page<FuelReport> fuelReportPage) {
        List<FuelReportDto> fuelReportDtoList = fuelReportPage.getContent().stream().map(mapper::toDto).collect(Collectors.toList());
        double totalSum = 0;
        double totalAmount = 0;
        for (FuelReportDto fuelReportDto : fuelReportDtoList) {
            double amount = fuelReportDto.getAmountAtStartOfShift() - fuelReportDto.getAmountAtEndOfShift();
            totalAmount += amount;
            totalAmount += (amount * fuelReportDto.getSalePrice());
        }
        return new FuelReportTotalDto(totalSum, totalAmount, fuelReportDtoList);
    }

    private FuelReportTotalDto getTotalFuelReport(List<FuelReport> fuelReportList) {
        List<FuelReportDto> fuelReportDtoList = fuelReportList.stream().map(mapper::toDto).collect(Collectors.toList());
        double totalSum = 0;
        double totalAmount = 0;
        for (FuelReportDto fuelReportDto : fuelReportDtoList) {
            double amount = fuelReportDto.getAmountAtStartOfShift() - fuelReportDto.getAmountAtEndOfShift();
            totalAmount += amount;
            totalAmount += (amount * fuelReportDto.getSalePrice());
        }
        return new FuelReportTotalDto(totalSum, totalAmount, fuelReportDtoList);
    }

}
