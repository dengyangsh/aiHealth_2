package com.aihealth.first.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aihealth.first.dto.CycleDietDto;
import com.aihealth.first.dto.CycleExerciseDto;
import com.aihealth.first.dto.FitnessCycleDto;
import com.aihealth.first.dto.UserDto;
import com.aihealth.first.entity.CycleDiet;
import com.aihealth.first.entity.CycleExercise;
import com.aihealth.first.entity.FitnessCycle;
import com.aihealth.first.entity.User;
import com.aihealth.first.repository.CycleDietRepository;
import com.aihealth.first.repository.CycleExerciseRepository;
import com.aihealth.first.repository.FitnessCycleRepository;
import com.aihealth.first.repository.UserRepository;
import com.aihealth.first.service.DeepSeekService;
import com.aihealth.first.service.InquiryService;
import com.aihealth.first.service.OllamaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class InquiryServiceImpl implements InquiryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FitnessCycleRepository fitnessCycleRepository;

    @Autowired
    private CycleDietRepository cycleDietRepository;

    @Autowired
    private CycleExerciseRepository cycleExerciseRepository;

    @Autowired
    private OllamaService ollamaService;
    @Autowired
    private DeepSeekService deepSeekService;

    private static String source = "deepseekapi";

    private static final String PROMPT_INSTRUCTIONS = "你是一个温柔专业的健身教练，对问题进行理解，结合用户的相关信息按照下面的要求进行处理。\n" +
            "1、如果是生成、修改、调整、变化等等此类的问题：questionType为调整修改;其他场景：questionType为普通提问。\n" +
            "2、如果客户要生成运动计划，没有指定运动天数则生成7天，如果客户指定天数超过7天也生成7天。\n" +
            "3、生成了几天的健身计划，就需要生成几天的饮食和运动；cycleDiet里的数据如果一个dietContent里面如果有2个食物，则需要生成2条记录，一个dietContent里面不能有2种食物！ 一个exerciseContent里面如果有2个运动，则需要生成2条记录！\n" +
            "4、如果用户问的是非健身问题，请更加温柔，细致的回答,尽量是对话询问式的,回答的内容放在answer字段里面；如果是健身问题，请更加专业，回答的内容放在answer字段里面，控制下回答的长度，不要超过500字，不低于50字\n"
            +
            "5、对比用户数据的json和输出的json，没有变化的key值一定不要输出！ json里面的fitnessCycle和user可以直接对比键值对；json里面的cycleDiet和cycleExercise是数组，对比时候id相同的才能做对比\n"+
            "6、严格按照要求的返回格式：(一个cycleExercise和cycleDiet是一个数组,对应了一个fitnessCycle运动周期内所有的运动和饮食记录,)";

    /**
     * 处理用户的健康咨询请求。根据用户ID查询用户信息和当前活跃的健身周期，
     * 结合用户的饮食计划和运动计划，为用户提供个性化的健康建议。
     * 如果用户没有活跃的健身周期，则仅基于用户基本信息提供建议。
     * 
     * @param userId   用户ID
     * @param question 用户查询的具体问题
     * @return 模型生成的个性化健康建议回答
     */
    @Override
    public String handleInquiry(Long userId, String question) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<FitnessCycle> activeCycle = fitnessCycleRepository.findByUserIdAndStatus(userId,
                    FitnessCycle.CycleStatus.ACTIVE);
            if (activeCycle.isPresent()) {
                // 查询饮食和运动计划
                FitnessCycle cycle = activeCycle.get();
                List<CycleDiet> dietList = cycleDietRepository.findByCycleId(cycle.getId());
                List<CycleExercise> exerciseList = cycleExerciseRepository.findByCycleId(cycle.getId());

                String systemPrompt = formatEntitySystemPrompt();

                // 使用新的格式化方法构建提示字符串
                String userPrompt = formatEntityUserPrompt(user, cycle,
                        dietList,
                        exerciseList);
                String modelResponse = getModelResponse(userId, question, systemPrompt, userPrompt);
                return processModelResponse(modelResponse, userId);
            }

            // 没有活跃的健身周期，直接返回模型的回答
            // 使用新的格式化方法，但只传入用户信息
            String systemPrompt = formatEntitySystemPrompt();
            String userPrompt = formatEntityUserPrompt(user, null, null, null);
            String modelResponse = getModelResponse(userId, question, systemPrompt, userPrompt);
            return processModelResponse(modelResponse, userId);
        }

        return "User not found";
    }

    private String getModelResponse(Long userId, String question, String systemPrompt, String userPrompt) {
        String modelResponse = "";
        if (source.equals("deepseekapi")) {
            modelResponse = deepSeekService.getDeepSeekResponse(systemPrompt, userPrompt, question, userId);
        } else {
            modelResponse = ollamaService.getPromptResponse(systemPrompt, userPrompt, question, userId);
        }
        return modelResponse;
    }

    /**
     * 将用户相关实体转换为格式化的提示字符串
     * 
     * @param user     用户实体
     * @param cycle    健身周期实体
     * @param diet     饮食实体
     * @param exercise 运动实体
     * @param question 用户问题
     * @return 格式化的提示字符串
     */
    public String formatEntityUserPrompt(User user, FitnessCycle cycle, List<CycleDiet> diets,
            List<CycleExercise> exercises) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        StringBuilder promptBuilder = new StringBuilder();

        try {
            // 2. 用户的相关信息
            promptBuilder.append("当前用户的相关信息：");
            Map<String, Object> userInfo = new HashMap<>();
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            userInfo.put("user", userDto);

            if(cycle != null){
                FitnessCycleDto fitnessCycleDto = new FitnessCycleDto();
                BeanUtils.copyProperties(cycle, fitnessCycleDto);
                userInfo.put("fitnessCycle", fitnessCycleDto);
            }

            if(diets != null){
                List<CycleDietDto> cycleDietDtoList = new ArrayList<>();
                for (CycleDiet diet : diets) {
                    CycleDietDto cycleDietDto = new CycleDietDto();
                    BeanUtils.copyProperties(diet, cycleDietDto);
                    cycleDietDtoList.add(cycleDietDto);
                }
                userInfo.put("cycleDiet", cycleDietDtoList);
            }

            if(exercises != null){
                List<CycleExerciseDto> cycleExerciseDtoList = new ArrayList<>();
                for (CycleExercise exercise : exercises) {
                    CycleExerciseDto cycleExerciseDto = new CycleExerciseDto();
                    BeanUtils.copyProperties(exercise, cycleExerciseDto);
                    cycleExerciseDtoList.add(cycleExerciseDto);
                }
                userInfo.put("cycleExercise", cycleExerciseDtoList);
            }

            promptBuilder.append(objectMapper.writeValueAsString(userInfo));
            promptBuilder.append("\n\n");

        } catch (JsonProcessingException e) {
            throw new RuntimeException("转换实体为JSON字符串时出错", e);
        }

        return promptBuilder.toString();
    }

    /**
     * 
     * @param user
     * @param cycle
     * @param diet
     * @param exercise
     * @param question
     * @return
     */
    public String formatEntitySystemPrompt() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        StringBuilder promptBuilder = new StringBuilder();

        try {
            // 1. 要求的返回格式
            promptBuilder.append(PROMPT_INSTRUCTIONS);
            Map<String, Object> formatExample = new HashMap<>();
            formatExample.put("question", createQuestion());
            formatExample.put("user", createUserFormat());
            formatExample.put("fitnessCycle", createFitnessCycleFormat());
            formatExample.put("cycleExercise", createCycleExerciseFormat());
            formatExample.put("cycleDiet", createCycleDietFormat());
            promptBuilder.append(objectMapper.writeValueAsString(formatExample));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("转换实体为JSON字符串时出错", e);
        }

        return promptBuilder.toString();
    }

    /**
     * 创建用户格式示例
     */
    private Map<String, String> createUserFormat() {
        Map<String, String> format = new HashMap<>();
        // format.put("id", "用户ID，Long类型");
        // format.put("username", "用户名，String类型，不能为空");
        format.put("age", "用户年龄，Integer类型，不能为空");
        format.put("height", "身高，Double类型，单位：厘米，不能为空");
        format.put("weight", "体重，Double类型，单位：千克，不能为空");
        format.put("bodyFatRate", "体脂率，Double类型，百分比");
        // format.put("scaleDeviceId", "电子秤设备ID，String类型");
        // format.put("speakerDeviceId", "智能音箱设备ID，String类型");
        return format;
    }

    /**
     * 创建用户格式示例
     */
    private Map<String, String> createQuestion() {
        Map<String, String> format = new HashMap<>();
        format.put("questionType", "问题类型，[普通提问,修改数据]");
        format.put("answer", "模型对问题的回答, String类型, 直接返回给客户");
        return format;
    }

    /**
     * 创建健身周期格式示例
     */
    private Map<String, String> createFitnessCycleFormat() {
        Map<String, String> format = new HashMap<>();
        // format.put("id", "健身周期ID，Long类型");
        // format.put("userId", "用户ID，Long类型，不能为空");
        // format.put("cycleNumber", "健身周期编号，String类型，不能为空");
        // format.put("status",
        // "健身周期状态，枚举类型：CONFIRMING(确认中)、PLANNED(计划中)、ACTIVE(进行中)、COMPLETED(已完成)、CANCELLED(已取消)");
        format.put("startTime", "健身周期开始时间，类似2025-03-12日期格式，不能为空");
        format.put("endTime", "健身周期结束时间，类似2025-03-12日期格式");
        format.put("durationDays", "健身周期持续时间（天），Integer类型");
        format.put("goal", "健身周期的目标，String类型");
        format.put("startWeight", "健身周期开始体重，Double类型");
        // format.put("endWeight", "健身周期结束体重，Double类型");
        return format;
    }

    /**
     * 创建运动记录格式示例
     */
    private List<Map<String, String>> createCycleExerciseFormat() {
        List<Map<String, String>> formats = new ArrayList<>();
        Map<String, String> format = new HashMap<>();
        format.put("id", "运动记录ID，Long类型");
        // format.put("cycleId", "健身周期ID，Long类型，不能为空");
        format.put("exerciseDate", "运动日期，类似2025-03-12日期格式，不能为空");
        format.put("exerciseTime", "运动时间，类似12:00:00时间格式，不能为空");
        format.put("exerciseContent", "运动内容，String类型，不能为空");
        format.put("durationMinutes", "运动时长（分钟），Integer类型");
        format.put("caloriesBurned", "消耗卡路里，Double类型");
        format.put("exerciseDescription", "运动的介绍，String类型");
        // format.put("plannedExerciseTime", "计划运动时间，LocalDateTime类型");
        formats.add(format);
        return formats;
    }

    /**
     * 创建饮食记录格式示例
     */
    private List<Map<String, String>> createCycleDietFormat() {
        List<Map<String, String>> formats = new ArrayList<>();
        Map<String, String> format = new HashMap<>();
        format.put("id", "饮食记录ID，Long类型");
        // format.put("cycleId", "健身周期ID，Long类型，不能为空");
        format.put("foodDate", "食物日期，类似2025-03-12日期格式，不能为空");
        format.put("mealTime",
                "用餐时间，枚举类型：BREAKFAST(早餐)、MORNING_SNACK(早上加餐)、LUNCH(午餐)、AFTERNOON_SNACK(下午加餐)、DINNER(晚餐)、EVENING_SNACK(晚上加餐)");
        format.put("foodQuantity", "食物量（克），Double类型");
        format.put("foodCategory", "食物类别，枚举类型:碳水,蛋白质,脂肪,纤维,维生素,矿物质,水,其他");
        format.put("calories", "食物的卡路里，Double类型");
        format.put("cookingMethod", "食物的做法，String类型");
        format.put("dietContent", "饮食内容，String类型");
        // format.put("caloriesIntake", "摄入卡路里，Double类型");
        format.put("dietDescription", "饮食描述，String类型");

        formats.add(format);
        return formats;
    }

    /**
     * 处理模型返回的响应
     * 
     * @param modelResponse 模型返回的JSON字符串
     * @param userId        用户ID
     * @return 处理后的响应
     */
    @SuppressWarnings("unchecked")
    private String processModelResponse(String modelResponse, Long userId) {
        // 尝试解析为JSON，如果失败则直接返回文本响应
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // 解析模型返回的JSON
            Map<String, Object> responseMap = objectMapper.readValue(modelResponse,
                    new TypeReference<Map<String, Object>>() {
                    });

            // 获取问题类型和回答
            Map<String, Object> questionMap = (Map<String, Object>) responseMap.get("question");
            if (questionMap == null) {
                return modelResponse; // 如果没有question字段，直接返回原始响应
            }

            String questionType = (String) questionMap.get("questionType");
            String answer = (String) questionMap.get("answer");

            // 如果是普通提问，直接返回answer
            if (questionType == null || "普通提问".equals(questionType)) {
                return answer != null ? answer : modelResponse;
            }

            // 如果是修改数据，处理需要修改的实体
            if ("调整修改".equals(questionType)) {
                Optional<User> userOptional = userRepository.findById(userId);
                if (!userOptional.isPresent()) {
                    return "用户不存在";
                }

                User user = userOptional.get();

                // 处理用户的修改
                if (responseMap.containsKey("user")) {
                    Map<String, Object> userMap = (Map<String, Object>) responseMap.get("user");
                    updateUserIfNeeded(user, userMap);
                }

                Optional<FitnessCycle> activeCycleOptional = fitnessCycleRepository.findByUserIdAndStatus(userId,
                        FitnessCycle.CycleStatus.ACTIVE);

                if (activeCycleOptional.isPresent()) {
                    FitnessCycle cycle = activeCycleOptional.get();

                    // 处理健身周期的修改
                    if (responseMap.containsKey("fitnessCycle")) {
                        Map<String, Object> cycleMap = (Map<String, Object>) responseMap.get("fitnessCycle");
                        updateFitnessCycleIfNeeded(cycle, cycleMap);
                    }

                    // 处理饮食记录的修改
                    if (responseMap.containsKey("cycleDiet")) {
                        List<Map<String, Object>> dietList = (List<Map<String, Object>>) responseMap.get("cycleDiet");
                        if (dietList != null && !dietList.isEmpty()) {
                            List<CycleDiet> existingDiets = cycleDietRepository.findByCycleId(cycle.getId());

                            if (!existingDiets.isEmpty()) {
                                // 如果已有饮食记录，更新第一条记录
                                for (Map<String, Object> dietMap : dietList) {
                                    // 检查是否有匹配的饮食记录（根据日期和用餐时间）
                                    boolean found = false;
                                    for (CycleDiet existingDiet : existingDiets) {
                                        if (matchesDietRecord(existingDiet, dietMap)) {
                                            updateCycleDietIfNeeded(existingDiet, dietMap);
                                            found = true;
                                            break;
                                        }
                                    }

                                    // 如果没有找到匹配的记录，创建新记录
                                    if (!found) {
                                        createCycleDiet(cycle.getId(), dietMap);
                                    }
                                }
                            } else {
                                // 如果没有饮食记录，创建新记录
                                for (Map<String, Object> dietMap : dietList) {
                                    createCycleDiet(cycle.getId(), dietMap);
                                }
                            }
                        }
                    }

                    // 处理运动记录的修改
                    if (responseMap.containsKey("cycleExercise")) {
                        List<Map<String, Object>> exerciseList = (List<Map<String, Object>>) responseMap
                                .get("cycleExercise");
                        if (exerciseList != null && !exerciseList.isEmpty()) {
                            List<CycleExercise> existingExercises = cycleExerciseRepository
                                    .findByCycleId(cycle.getId());

                            if (!existingExercises.isEmpty()) {
                                // 如果已有运动记录，尝试更新匹配的记录
                                for (Map<String, Object> exerciseMap : exerciseList) {
                                    // 检查是否有匹配的运动记录（根据日期和时间）
                                    boolean found = false;
                                    for (CycleExercise existingExercise : existingExercises) {
                                        if (matchesExerciseRecord(existingExercise, exerciseMap)) {
                                            updateCycleExerciseIfNeeded(existingExercise, exerciseMap);
                                            found = true;
                                            break;
                                        }
                                    }

                                    // 如果没有找到匹配的记录，创建新记录
                                    if (!found) {
                                        createCycleExercise(cycle.getId(), exerciseMap);
                                    }
                                }
                            } else {
                                // 如果没有运动记录，创建新记录
                                for (Map<String, Object> exerciseMap : exerciseList) {
                                    createCycleExercise(cycle.getId(), exerciseMap);
                                }
                            }
                        }
                    }
                } else {
                    // 用户没有激活中的健身周期，创建新的健身周期、饮食和运动计划
                    if (responseMap.containsKey("fitnessCycle")) {
                        // 创建新的健身周期
                        FitnessCycle newCycle = createFitnessCycle(userId,
                                (Map<String, Object>) responseMap.get("fitnessCycle"));
                        if (newCycle != null) {
                            // 创建饮食记录
                            if (responseMap.containsKey("cycleDiet")) {
                                List<Map<String, Object>> dietList = (List<Map<String, Object>>) responseMap
                                        .get("cycleDiet");
                                if (dietList != null && !dietList.isEmpty()) {
                                    for (Map<String, Object> dietMap : dietList) {
                                        createCycleDiet(newCycle.getId(), dietMap);
                                    }
                                }
                            }

                            // 创建运动记录
                            if (responseMap.containsKey("cycleExercise")) {
                                List<Map<String, Object>> exerciseList = (List<Map<String, Object>>) responseMap
                                        .get("cycleExercise");
                                if (exerciseList != null && !exerciseList.isEmpty()) {
                                    for (Map<String, Object> exerciseMap : exerciseList) {
                                        createCycleExercise(newCycle.getId(), exerciseMap);
                                    }
                                }
                            }
                        }
                    }
                }

                return answer != null ? answer : "数据已更新";
            }

            return answer;

        } catch (Exception e) {
            // JSON解析失败，直接返回原始响应
            return modelResponse;
        }
    }

    /**
     * 更新用户信息（如果需要）
     */
    private void updateUserIfNeeded(User user, Map<String, Object> userMap) {
        boolean updated = false;

        if (userMap.containsKey("age") && userMap.get("age") != null) {
            user.setAge(((Number) userMap.get("age")).intValue());
            updated = true;
        }

        if (userMap.containsKey("height") && userMap.get("height") != null) {
            user.setHeight(((Number) userMap.get("height")).doubleValue());
            updated = true;
        }

        if (userMap.containsKey("weight") && userMap.get("weight") != null) {
            user.setWeight(((Number) userMap.get("weight")).doubleValue());
            updated = true;
        }

        if (userMap.containsKey("bodyFatRate") && userMap.get("bodyFatRate") != null) {
            user.setBodyFatRate(((Number) userMap.get("bodyFatRate")).doubleValue());
            updated = true;
        }
 
        if (updated) {
            userRepository.save(user);
        }
    }

    /**
     * 更新健身周期信息（如果需要）
     */
    private void updateFitnessCycleIfNeeded(FitnessCycle cycle, Map<String, Object> cycleMap) {
        boolean updated = false;

        if (cycleMap.containsKey("goal") && cycleMap.get("goal") != null) {
            cycle.setGoal((String) cycleMap.get("goal"));
            updated = true;
        }

        if (cycleMap.containsKey("status") && cycleMap.get("status") != null) {
            try {
                cycle.setStatus(FitnessCycle.CycleStatus.valueOf((String) cycleMap.get("status")));
                updated = true;
            } catch (IllegalArgumentException e) {
                // 状态值无效，忽略
            }
        }

        if (cycleMap.containsKey("endTime") && cycleMap.get("endTime") != null) {
            // 处理日期时间字符串转换
            try {
                String endTimeStr = (String) cycleMap.get("endTime");
                LocalDateTime endTime = LocalDateTime.parse(endTimeStr);
                cycle.setEndTime(endTime);
                updated = true;
            } catch (Exception e) {
                // 日期格式错误，忽略
            }
        }

        if (cycleMap.containsKey("durationDays") && cycleMap.get("durationDays") != null) {
            cycle.setDurationDays(((Number) cycleMap.get("durationDays")).intValue());
            updated = true;
        }

        if (cycleMap.containsKey("endWeight") && cycleMap.get("endWeight") != null) {
            cycle.setEndWeight(((Number) cycleMap.get("endWeight")).doubleValue());
            updated = true;
        }

        if (updated) {
            fitnessCycleRepository.save(cycle);
        }
    }

    /**
     * 更新饮食记录信息（如果需要）
     */
    private void updateCycleDietIfNeeded(CycleDiet diet, Map<String, Object> dietMap) {
        boolean updated = false;

        if (dietMap.containsKey("dietContent") && dietMap.get("dietContent") != null) {
            diet.setDietContent((String) dietMap.get("dietContent"));
            updated = true;
        }

        if (dietMap.containsKey("caloriesIntake") && dietMap.get("caloriesIntake") != null) {
            diet.setCaloriesIntake(((Number) dietMap.get("caloriesIntake")).doubleValue());
            updated = true;
        }

        if (dietMap.containsKey("dietDescription") && dietMap.get("dietDescription") != null) {
            diet.setDietDescription((String) dietMap.get("dietDescription"));
            updated = true;
        }

        if (dietMap.containsKey("foodCategory") && dietMap.get("foodCategory") != null) {
            diet.setFoodCategory((String) dietMap.get("foodCategory"));
            updated = true;
        }

        if (dietMap.containsKey("foodQuantity") && dietMap.get("foodQuantity") != null) {
            diet.setFoodQuantity(((Number) dietMap.get("foodQuantity")).doubleValue());
            updated = true;
        }

        if (dietMap.containsKey("calories") && dietMap.get("calories") != null) {
            diet.setCalories(((Number) dietMap.get("calories")).doubleValue());
            updated = true;
        }

        if (dietMap.containsKey("cookingMethod") && dietMap.get("cookingMethod") != null) {
            diet.setCookingMethod((String) dietMap.get("cookingMethod"));
            updated = true;
        }

        if (dietMap.containsKey("mealTime") && dietMap.get("mealTime") != null) {
            try {
                diet.setMealTime(CycleDiet.MealTime.valueOf((String) dietMap.get("mealTime")));
                updated = true;
            } catch (IllegalArgumentException e) {
                // 无效的枚举值，忽略
            }
        }

        if (updated) {
            cycleDietRepository.save(diet);
        }
    }

    /**
     * 更新运动记录信息（如果需要）
     */
    private void updateCycleExerciseIfNeeded(CycleExercise exercise, Map<String, Object> exerciseMap) {
        boolean updated = false;

        if (exerciseMap.containsKey("exerciseContent") && exerciseMap.get("exerciseContent") != null) {
            exercise.setExerciseContent((String) exerciseMap.get("exerciseContent"));
            updated = true;
        }

        if (exerciseMap.containsKey("durationMinutes") && exerciseMap.get("durationMinutes") != null) {
            exercise.setDurationMinutes(((Number) exerciseMap.get("durationMinutes")).intValue());
            updated = true;
        }

        if (exerciseMap.containsKey("caloriesBurned") && exerciseMap.get("caloriesBurned") != null) {
            exercise.setCaloriesBurned(((Number) exerciseMap.get("caloriesBurned")).doubleValue());
            updated = true;
        }

        if (exerciseMap.containsKey("exerciseDescription") && exerciseMap.get("exerciseDescription") != null) {
            exercise.setExerciseDescription((String) exerciseMap.get("exerciseDescription"));
            updated = true;
        }

        if (exerciseMap.containsKey("exerciseDate") && exerciseMap.get("exerciseDate") != null) {
            try {
                String dateStr = (String) exerciseMap.get("exerciseDate");
                LocalDate date = LocalDate.parse(dateStr);
                exercise.setExerciseDate(date);
                updated = true;
            } catch (Exception e) {
                // 日期格式错误，忽略
            }
        }

        if (exerciseMap.containsKey("exerciseTime") && exerciseMap.get("exerciseTime") != null) {
            try {
                String timeStr = (String) exerciseMap.get("exerciseTime");
                LocalTime time = LocalTime.parse(timeStr);
                exercise.setExerciseTime(time);
                updated = true;
            } catch (Exception e) {
                // 时间格式错误，忽略
            }
        }

        if (exerciseMap.containsKey("plannedExerciseTime") && exerciseMap.get("plannedExerciseTime") != null) {
            try {
                String dateTimeStr = (String) exerciseMap.get("plannedExerciseTime");
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
                exercise.setPlannedExerciseTime(dateTime);
                updated = true;
            } catch (Exception e) {
                // 日期时间格式错误，忽略
            }
        }

        if (updated) {
            cycleExerciseRepository.save(exercise);
        }
    }

    /**
     * 创建新的健身周期
     */
    private FitnessCycle createFitnessCycle(Long userId, Map<String, Object> cycleMap) {
        try {
            FitnessCycle cycle = new FitnessCycle();
            cycle.setUserId(userId);

            // 生成周期编号
            String cycleNumber = "CYCLE-" + userId + "-" + System.currentTimeMillis();
            cycle.setCycleNumber(cycleNumber);

            // 设置状态为激活
            cycle.setStatus(FitnessCycle.CycleStatus.ACTIVE);

            // 设置开始时间
            LocalDateTime startTime = LocalDateTime.now();
            if (cycleMap.containsKey("startTime") && cycleMap.get("startTime") != null) {
                try {
                    String startTimeStr = (String) cycleMap.get("startTime");
                    // 尝试解析日期格式
                    if (startTimeStr.contains("T")) {
                        startTime = LocalDateTime.parse(startTimeStr);
                    } else {
                        // 如果只有日期部分，添加时间部分
                        startTime = LocalDate.parse(startTimeStr).atStartOfDay();
                    }
                } catch (Exception e) {
                    // 日期格式错误，使用当前时间
                }
            }
            cycle.setStartTime(startTime);

            // 设置结束时间
            if (cycleMap.containsKey("endTime") && cycleMap.get("endTime") != null) {
                try {
                    String endTimeStr = (String) cycleMap.get("endTime");
                    LocalDateTime endTime;
                    if (endTimeStr.contains("T")) {
                        endTime = LocalDateTime.parse(endTimeStr);
                    } else {
                        // 如果只有日期部分，添加时间部分
                        endTime = LocalDate.parse(endTimeStr).atStartOfDay();
                    }
                    cycle.setEndTime(endTime);
                } catch (Exception e) {
                    // 日期格式错误，忽略
                }
            }

            // 设置持续天数
            if (cycleMap.containsKey("durationDays") && cycleMap.get("durationDays") != null) {
                try {
                    cycle.setDurationDays(((Number) cycleMap.get("durationDays")).intValue());
                } catch (Exception e) {
                    // 类型转换错误，忽略
                }
            } else if (cycle.getEndTime() != null) {
                // 如果没有明确设置持续天数，但有结束时间，计算持续天数
                long days = ChronoUnit.DAYS.between(cycle.getStartTime().toLocalDate(),
                        cycle.getEndTime().toLocalDate());
                cycle.setDurationDays((int) days);
            } else {
                // 默认设置为7天
                cycle.setDurationDays(7);
                // 设置结束时间为开始时间加7天
                cycle.setEndTime(cycle.getStartTime().plusDays(7));
            }

            // 设置目标
            if (cycleMap.containsKey("goal") && cycleMap.get("goal") != null) {
                cycle.setGoal((String) cycleMap.get("goal"));
            } else {
                // 目标是必填字段，设置默认值
                cycle.setGoal("保持健康");
            }

            // 设置开始体重
            if (cycleMap.containsKey("startWeight") && cycleMap.get("startWeight") != null) {
                try {
                    cycle.setStartWeight(((Number) cycleMap.get("startWeight")).doubleValue());
                } catch (Exception e) {
                    // 类型转换错误，忽略
                }
            }

            // 设置结束体重
            if (cycleMap.containsKey("endWeight") && cycleMap.get("endWeight") != null) {
                try {
                    cycle.setEndWeight(((Number) cycleMap.get("endWeight")).doubleValue());
                } catch (Exception e) {
                    // 类型转换错误，忽略
                }
            }

            // 保存健身周期
            return fitnessCycleRepository.save(cycle);
        } catch (Exception e) {
            // 创建健身周期失败
            return null;
        }
    }

    /**
     * 创建新的饮食记录
     */
    private CycleDiet createCycleDiet(Long cycleId, Map<String, Object> dietMap) {
        try {
            CycleDiet diet = new CycleDiet();
            diet.setCycleId(cycleId);

            // 设置食物日期
            LocalDate foodDate = LocalDate.now();
            if (dietMap.containsKey("foodDate") && dietMap.get("foodDate") != null) {
                try {
                    String foodDateStr = (String) dietMap.get("foodDate");
                    foodDate = LocalDate.parse(foodDateStr);
                } catch (Exception e) {
                    // 日期格式错误，使用当前日期
                }
            }
            diet.setFoodDate(foodDate);

            // 设置用餐时间
            if (dietMap.containsKey("mealTime") && dietMap.get("mealTime") != null) {
                try {
                    String mealTimeStr = (String) dietMap.get("mealTime");
                    diet.setMealTime(CycleDiet.MealTime.valueOf(mealTimeStr));
                } catch (Exception e) {
                    // 枚举值错误，设置默认值
                    diet.setMealTime(CycleDiet.MealTime.BREAKFAST);
                }
            } else {
                // 用餐时间是必填字段，设置默认值
                diet.setMealTime(CycleDiet.MealTime.BREAKFAST);
            }

            // 设置食物量
            if (dietMap.containsKey("foodQuantity") && dietMap.get("foodQuantity") != null) {
                try {
                    diet.setFoodQuantity(((Number) dietMap.get("foodQuantity")).doubleValue());
                } catch (Exception e) {
                    // 类型转换错误，忽略
                }
            }

            // 设置食物类别
            if (dietMap.containsKey("foodCategory") && dietMap.get("foodCategory") != null) {
                diet.setFoodCategory((String) dietMap.get("foodCategory"));
            } else {
                // 食物类别是必填字段，设置默认值
                diet.setFoodCategory("其他");
            }

            // 设置卡路里
            if (dietMap.containsKey("calories") && dietMap.get("calories") != null) {
                try {
                    diet.setCalories(((Number) dietMap.get("calories")).doubleValue());
                } catch (Exception e) {
                    // 类型转换错误，忽略
                }
            }

            // 设置烹饪方法
            if (dietMap.containsKey("cookingMethod") && dietMap.get("cookingMethod") != null) {
                diet.setCookingMethod((String) dietMap.get("cookingMethod"));
            }

            // 设置饮食内容
            if (dietMap.containsKey("dietContent") && dietMap.get("dietContent") != null) {
                diet.setDietContent((String) dietMap.get("dietContent"));
            }

            // 设置卡路里摄入量
            if (dietMap.containsKey("caloriesIntake") && dietMap.get("caloriesIntake") != null) {
                try {
                    diet.setCaloriesIntake(((Number) dietMap.get("caloriesIntake")).doubleValue());
                } catch (Exception e) {
                    // 类型转换错误，忽略
                }
            }

            // 设置饮食描述
            if (dietMap.containsKey("dietDescription") && dietMap.get("dietDescription") != null) {
                diet.setDietDescription((String) dietMap.get("dietDescription"));
            }

            // 保存饮食记录
            return cycleDietRepository.save(diet);
        } catch (Exception e) {
            // 创建饮食记录失败
            return null;
        }
    }

    /**
     * 创建新的运动记录
     */
    private CycleExercise createCycleExercise(Long cycleId, Map<String, Object> exerciseMap) {
        try {
            CycleExercise exercise = new CycleExercise();
            exercise.setCycleId(cycleId);

            // 设置运动日期
            LocalDate exerciseDate = LocalDate.now();
            if (exerciseMap.containsKey("exerciseDate") && exerciseMap.get("exerciseDate") != null) {
                try {
                    String exerciseDateStr = (String) exerciseMap.get("exerciseDate");
                    exerciseDate = LocalDate.parse(exerciseDateStr);
                } catch (Exception e) {
                    // 日期格式错误，使用当前日期
                }
            }
            exercise.setExerciseDate(exerciseDate);

            // 设置运动时间
            LocalTime exerciseTime = LocalTime.now();
            if (exerciseMap.containsKey("exerciseTime") && exerciseMap.get("exerciseTime") != null) {
                try {
                    String exerciseTimeStr = (String) exerciseMap.get("exerciseTime");
                    exerciseTime = LocalTime.parse(exerciseTimeStr);
                } catch (Exception e) {
                    // 时间格式错误，使用当前时间
                }
            }
            exercise.setExerciseTime(exerciseTime);

            // 设置运动内容
            if (exerciseMap.containsKey("exerciseContent") && exerciseMap.get("exerciseContent") != null) {
                exercise.setExerciseContent((String) exerciseMap.get("exerciseContent"));
            } else {
                // 运动内容是必填字段，设置默认值
                exercise.setExerciseContent("常规锻炼");
            }

            // 设置运动时长
            if (exerciseMap.containsKey("durationMinutes") && exerciseMap.get("durationMinutes") != null) {
                try {
                    exercise.setDurationMinutes(((Number) exerciseMap.get("durationMinutes")).intValue());
                } catch (Exception e) {
                    // 类型转换错误，忽略
                }
            }

            // 设置消耗卡路里
            if (exerciseMap.containsKey("caloriesBurned") && exerciseMap.get("caloriesBurned") != null) {
                try {
                    exercise.setCaloriesBurned(((Number) exerciseMap.get("caloriesBurned")).doubleValue());
                } catch (Exception e) {
                    // 类型转换错误，忽略
                }
            }

            // 设置运动描述
            if (exerciseMap.containsKey("exerciseDescription") && exerciseMap.get("exerciseDescription") != null) {
                exercise.setExerciseDescription((String) exerciseMap.get("exerciseDescription"));
            }

            // 设置计划运动时间
            if (exerciseMap.containsKey("plannedExerciseTime") && exerciseMap.get("plannedExerciseTime") != null) {
                try {
                    String plannedTimeStr = (String) exerciseMap.get("plannedExerciseTime");
                    LocalDateTime plannedTime;
                    if (plannedTimeStr.contains("T")) {
                        plannedTime = LocalDateTime.parse(plannedTimeStr);
                    } else {
                        // 如果只有日期部分，添加时间部分
                        plannedTime = LocalDate.parse(plannedTimeStr).atTime(exerciseTime);
                    }
                    exercise.setPlannedExerciseTime(plannedTime);
                } catch (Exception e) {
                    // 日期时间格式错误，忽略
                }
            }

            // 保存运动记录
            return cycleExerciseRepository.save(exercise);
        } catch (Exception e) {
            // 创建运动记录失败
            return null;
        }
    }

    /**
     * 检查饮食记录是否匹配
     * 
     * @param diet    现有饮食记录
     * @param dietMap 新的饮食数据
     * @return 是否匹配
     */
    private boolean matchesDietRecord(CycleDiet diet, Map<String, Object> dietMap) {
        // 如果没有日期或用餐时间信息，无法匹配
        if (!dietMap.containsKey("foodDate") || !dietMap.containsKey("mealTime")) {
            return false;
        }

        try {
            // 获取日期
            String foodDateStr = (String) dietMap.get("foodDate");
            LocalDate foodDate = LocalDate.parse(foodDateStr);

            // 获取用餐时间
            String mealTimeStr = (String) dietMap.get("mealTime");
            CycleDiet.MealTime mealTime = CycleDiet.MealTime.valueOf(mealTimeStr);

            // 检查日期和用餐时间是否匹配
            return diet.getFoodDate().equals(foodDate) && diet.getMealTime() == mealTime;
        } catch (Exception e) {
            // 解析错误，无法匹配
            return false;
        }
    }

    /**
     * 检查运动记录是否匹配
     * 
     * @param exercise    现有运动记录
     * @param exerciseMap 新的运动数据
     * @return 是否匹配
     */
    private boolean matchesExerciseRecord(CycleExercise exercise, Map<String, Object> exerciseMap) {
        // 如果没有日期或时间信息，无法匹配
        if (!exerciseMap.containsKey("exerciseDate") || !exerciseMap.containsKey("exerciseTime")) {
            return false;
        }

        try {
            // 获取日期
            String exerciseDateStr = (String) exerciseMap.get("exerciseDate");
            LocalDate exerciseDate = LocalDate.parse(exerciseDateStr);

            // 获取时间
            String exerciseTimeStr = (String) exerciseMap.get("exerciseTime");
            LocalTime exerciseTime = LocalTime.parse(exerciseTimeStr);

            // 检查日期和时间是否匹配
            return exercise.getExerciseDate().equals(exerciseDate) && exercise.getExerciseTime().equals(exerciseTime);
        } catch (Exception e) {
            // 解析错误，无法匹配
            return false;
        }
    }
}