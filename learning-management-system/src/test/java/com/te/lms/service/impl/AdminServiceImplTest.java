package com.te.lms.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.common.collect.Lists;
import com.te.lms.entity.Admin;
import com.te.lms.entity.AppUser;
import com.te.lms.entity.Mentor;
import com.te.lms.entity.Roles;
import com.te.lms.entity.Skills;
import com.te.lms.entity.dto.MessageDto;
import com.te.lms.entity.dto.NewMentorDto;
import com.te.lms.entity.dto.SkillsDto;
import com.te.lms.repository.AdminRepository;
import com.te.lms.repository.AppUserRepository;
import com.te.lms.repository.BatchRepository;
import com.te.lms.repository.EmployeeRepository;
import com.te.lms.repository.MentorRepositorty;
import com.te.lms.repository.RequestRepository;
import com.te.lms.repository.RoleRepository;
import com.te.lms.repository.TechnologiesRepository;

@SpringBootTest
class AdminServiceImplTest {
	@Mock
	private MentorRepositorty mentorRepository;

	@Mock
	private AppUserRepository appUserRepository;

	@Mock
	private AdminRepository adminRepository;

	@Mock
	private RoleRepository rolesRepository;

	@Mock
	private BatchRepository batchRepository;

	@Mock
	private TechnologiesRepository technologiesRepository;

	@Mock
	private RequestRepository requestsRepository;

	@Mock
	private EmployeeRepository employeeRepository;

	@InjectMocks
	private AdminServiceImpl adminService;

	@BeforeEach
	public void setUp() {
		Roles employee = Roles.builder().roleName("ROLE_EMPLOYEE").build();
		Roles mentor = Roles.builder().roleName("ROLE_MENTOR").build();
		Roles admin = Roles.builder().roleName("ROLE_ADMIN").appUser(Lists.newArrayList()).build();

		Admin admin01 = Admin.builder().adminId("TYC001").adminName("Vinay").build();

		AppUser adminCredentials = AppUser.builder().userName(admin01.getAdminId()).password("password")
				.roles(Lists.newArrayList()).build();

		admin.getAppUser().add(adminCredentials);
		adminCredentials.getRoles().add(admin);

		rolesRepository.save(employee);
		rolesRepository.save(mentor);
		rolesRepository.save(admin);
		adminRepository.save(admin01);

		appUserRepository.save(adminCredentials);
	}

	@Test
	public void testRegisterMentor() {

		NewMentorDto mentorDto = NewMentorDto.builder().mentorEmployeeId("TY001").mentorEmailId("mentor@gmail.com")
				.skillsDto(List.of(SkillsDto.builder().skillName("java").build())).mentorName("Vinay").build();
		Mentor mentor = Mentor.builder().mentorId("TY001").emailId("mentor@gmail.com")
				.skills(List.of(Skills.builder().skillName("java").build())).mentorName("Vinay").build();
		AppUser appUser = AppUser.builder().userName(mentorDto.getMentorEmployeeId()).password("Welcome123")
				.roles(Lists.newArrayList()).build();
		Roles role = Roles.builder().roleName("ROLE_MENTOR").appUser(Lists.newArrayList()).build();
		appUser.getRoles().add(role);
		role.getAppUser().add(appUser);
		Mockito.when(rolesRepository.findByRoleName(Mockito.any())).thenReturn(Optional.ofNullable(role));
		Mockito.when(appUserRepository.save(Mockito.any())).thenReturn(appUser);
		Mockito.when(mentorRepository.save(Mockito.any())).thenReturn(mentor);
		String message = "Hello " + mentor.getMentorName() + " \n" + " welcome to the team of techno elevate"
				+ "username : " + appUser.getUserName() + "\n" + " password :" + appUser.getPassword();
		MessageDto messageDto = MessageDto.builder().message(message).emailId(mentor.getEmailId()).build();
		Optional<String> messageDto2 = adminService.registerMentor(mentorDto);
		assertEquals(messageDto2.get(), mentor.getMentorName());

	}

	@Test
	public void testDeleteMentor_ReturnsTrue() {
		Mentor mentor = Mentor.builder().mentorId("TY001").emailId("a@a").mentorName("vinay").skills(null).build();
		Mockito.when(mentorRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(mentor));
		Mockito.when(mentorRepository.save(Mockito.any())).thenReturn(mentor);
		Boolean isDeleted = adminService.deleteMentor(Mockito.any());
		assertEquals(true, isDeleted);
	}

	@Test
	public void testDeleteMentor_ReturnsFalse() {
		Mentor mentor = Mentor.builder().mentorId("TY001").emailId("mentor@gmail.com")
				.skills(List.of(Skills.builder().skillName("java").build())).mentorName("Rakesh").build();
		Mockito.when(mentorRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
		Mockito.when(mentorRepository.save(Mockito.any())).thenReturn(mentor);
		Boolean isDeleted = adminService.deleteMentor(Mockito.any());
		assertFalse(isDeleted);
	}

	@Test
	public void testReadMentor() {
		Mentor mentor = Mentor.builder().mentorId("Ty01").mentorName("Ram").emailId("a@a")
				.skills(List.of(Skills.builder().skillName("java").build())).build();
		NewMentorDto nmd = new NewMentorDto();
		Mockito.when(mentorRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(mentor));

		Optional<NewMentorDto> read = adminService.read("Ty01");
		BeanUtils.copyProperties(read.get(), nmd);

		assertEquals(mentor.getMentorId(), nmd.getMentorEmployeeId());
	}

}