# Git 协作开发工作流方案 (两人团队版)

## 🌳 简化分支策略

### 核心分支架构
```
main (生产环境)
└── develop (开发分支)
    └── feature/* (可选功能分支)
```

### 🎯 分支职责说明

#### 主要分支
- **`main`** - 生产环境分支
  - ✅ 稳定可发布的代码
  - 🏷️ 发布时创建版本标签
  - 📋 仅通过develop合并

- **`develop`** - 日常开发分支
  - 🔄 所有开发工作的主分支
  - 📝 可以直接提交小改动
  - 🧪 定期合并到main发布

#### 临时分支 (可选)
- **`feature/*`** - 大功能开发
  - 📝 仅在开发复杂功能时使用
  - 🎯 完成后合并回develop
  - ⏰ 开发周期超过1天的功能建议使用

## 📝 简化提交信息规范

### 基本格式
```
<类型>: <描述>

示例:
feat: 添加向量搜索功能
fix: 修复分页bug
docs: 更新README
```

### 🏷️ 提交类型
- **feat**: 新功能
- **fix**: 修复bug  
- **docs**: 文档更新
- **refactor**: 代码重构
- **test**: 测试相关

### 📋 提交示例
```bash
# ✅ 简洁明了
feat: 添加Elasticsearch搜索接口
fix: 修复MySQL连接超时问题
docs: 更新开发环境配置说明

# ❌ 太简单
update
fix bug
add feature
```

## 🔄 简化工作流程

### 🚀 日常开发流程

#### 1. 小改动（推荐）
```bash
# 直接在develop分支开发
git checkout develop
git pull origin develop

# 进行开发
# ...

# 提交变更
git add .
git commit -m "feat: 添加搜索历史功能"
git push origin develop
```

#### 2. 大功能开发
```bash
# 创建功能分支
git checkout develop
git pull origin develop
git checkout -b feature/complex-search

# 开发过程中定期同步
git add .
git commit -m "feat: 实现复杂搜索逻辑"
git push origin feature/complex-search

# 完成后合并
git checkout develop
git merge feature/complex-search
git push origin develop
git branch -d feature/complex-search
git push origin --delete feature/complex-search
```

### 🚀 发布流程

#### 准备发布
```bash
# 从develop合并到main
git checkout main
git pull origin main
git merge develop

# 创建版本标签
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main
git push origin v1.0.0
```

### 🛠️ 冲突解决
```bash
# 如果出现合并冲突
git checkout develop
git pull origin develop
# 解决冲突文件
git add .
git commit -m "fix: 解决合并冲突"
git push origin develop
```

## 🛡️ 简化分支保护

### main分支保护 (可选)
```yaml
基本保护:
  - 禁止直接推送到main
  - 仅允许从develop合并
```

## 👥 两人协作规范

### 📋 职责分工
- **主开发者**: 负责核心功能和架构设计
- **协作开发者**: 负责功能完善和测试

### 📝 沟通约定
1. 🗺️ **日站会**: 每日简单同步进度
2. 📝 **代码审查**: 重要变更互相审查
3. 🧪 **测试保障**: 提交前运行基本测试

### 📋 Pull Request 规范

#### PR标题格式
```
[功能类型] 简洁描述 (#Issue编号)

示例:
[Feature] 添加向量搜索API (#123)
[Bugfix] 修复分页参数验证问题 (#124)
[Refactor] 重构SearchService架构 (#125)
```

#### PR描述模板
```markdown
## 📝 变更说明
<!-- 详细描述本次变更内容 -->

## 🎯 解决问题
<!-- 关联的Issue或问题描述 -->
Closes #123

## 🧪 测试说明
<!-- 如何测试本次变更 -->
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 手动测试场景

## ⚠️ 注意事项
<!-- 部署或使用时需要注意的事项 -->

## 📸 截图/演示
<!-- 如有必要，提供截图或演示 -->
```

#### 代码审查要点
- 🏗️ 架构设计合理性
- 🧪 测试覆盖充分性
- 📚 文档完整性
- 🔒 安全性考虑
- ⚡ 性能影响评估

## 🔧 Git配置建议

### 个人Git配置
```bash
# 基本信息配置
git config --global user.name "你的姓名"
git config --global user.email "your.email@company.com"

# 提交签名（推荐）
git config --global commit.gpgsign true

# 推送策略
git config --global push.default simple

# 自动换行处理
git config --global core.autocrlf true  # Windows
git config --global core.autocrlf input # Mac/Linux

# 文件权限处理
git config --global core.filemode false # Windows
```

### 项目级别配置
```bash
# 在项目根目录执行
git config core.hooksPath .githooks  # 使用项目Git hooks
git config branch.autosetupmerge always
git config branch.autosetuprebase always
```

## 🚨 冲突解决策略

### 预防冲突
1. 🔄 **频繁同步**: 每日开始工作前先同步develop
2. 📦 **小步提交**: 保持提交粒度小且专注
3. 🗺️ **任务分工**: 避免多人同时修改同一文件

### 冲突解决步骤
```bash
# 1. 获取最新代码
git checkout develop
git pull origin develop

# 2. 切换到功能分支
git checkout feature/your-feature

# 3. rebase到最新develop
git rebase develop

# 4. 如有冲突，逐个解决
git add <resolved-file>
git rebase --continue

# 5. 强制推送（谨慎使用）
git push --force-with-lease origin feature/your-feature
```

### 冲突解决工具推荐
- 🔧 **VS Code**: 内置Git合并工具
- 🎯 **Beyond Compare**: 专业文件对比工具
- 🌟 **GitKraken**: 可视化Git客户端
- ⚙️ **命令行**: git mergetool

## 📊 持续集成配置

### GitHub Actions示例
```yaml
# .github/workflows/ci.yml
name: CI Pipeline
on:
  push:
    branches: [ main, develop, test ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Run tests
      run: ./mvnw clean test
    
    - name: Generate test report
      uses: dorny/test-reporter@v1
      if: success() || failure()
      with:
        name: Maven Tests
        path: target/surefire-reports/*.xml
        reporter: java-junit
```

## 🎯 最佳实践总结

### ✅ 推荐做法
1. 🔄 **定期同步**: 每天开始工作前同步最新代码
2. 📝 **清晰提交**: 使用规范的提交信息
3. 🧪 **测试驱动**: 提交前确保测试通过
4. 👥 **积极审查**: 认真对待代码审查
5. 📚 **文档同步**: 代码变更及时更新文档

### ❌ 避免做法
1. 🚫 **直接推送**: 绝不直接推送到受保护分支
2. 🙅 **巨大提交**: 避免单次提交包含过多变更
3. 💔 **忽略冲突**: 不要强制覆盖他人代码
4. 📵 **跳过测试**: 不要跳过CI检查直接合并
5. 🤐 **无声开发**: 避免长时间不交流的孤立开发

## 🚀 快速上手命令

### 日常开发命令
```bash
# 1. 开始新功能开发
git checkout develop && git pull origin develop
git checkout -b feature/your-feature-name
git push -u origin feature/your-feature-name

# 2. 日常提交
git add .
git commit -m "feat(scope): 描述你的变更"
git push origin feature/your-feature-name

# 3. 同步最新代码
git checkout develop && git pull origin develop
git checkout feature/your-feature-name
git rebase develop
git push --force-with-lease origin feature/your-feature-name

# 4. 完成开发，创建PR
# 通过Web界面创建Pull Request
```

### 紧急修复命令
```bash
# 1. 创建hotfix分支
git checkout main && git pull origin main
git checkout -b hotfix/critical-bug-fix
git push -u origin hotfix/critical-bug-fix

# 2. 修复并测试
# ... 进行修复 ...
git add . && git commit -m "fix: 修复关键生产问题"
git push origin hotfix/critical-bug-fix

# 3. 通过PR合并到main和develop
```

---

📋 **使用说明**: 本工作流程适用于团队规模3-10人的项目开发，可根据实际情况调整分支策略和审查流程。