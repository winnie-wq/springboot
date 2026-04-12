-- 示例数据：在 Navicat 中选中库 medicinal_plant 后整段执行
-- 登录测试账号：用户名 admin 或 demo，密码均为 123456（与 Spring BCryptPasswordEncoder 一致）

SET NAMES utf8mb4;

-- 1) sys_user：系统用户（登录、鉴权）
INSERT INTO sys_user (username, password_hash, nickname) VALUES
('admin', '$2a$10$RZKe6O/ZQtCS4qKxBci9GedfueOjYKO1XmDyQBAMAGltWJOl/rKne', '管理员'),
('demo', '$2a$10$RZKe6O/ZQtCS4qKxBci9GedfueOjYKO1XmDyQBAMAGltWJOl/rKne', '演示用户');

-- 2) plant_category：植物分类（树形，parent_id 指向父分类，NULL 表示顶级）
INSERT INTO plant_category (name, emoji, parent_id, description) VALUES
('根茎类', '🪵', NULL, '以根或根茎入药的植物'),
('全草类', '🌿', NULL, '以地上全草入药的植物'),
('花果类', '🌸', NULL, '以花或果实入药的植物');

-- 3) plant_species：具体物种（category_id 必须存在于 plant_category）
INSERT INTO plant_species (category_id, name, scientific_name, habitat, diet, features, conservation_status, rating, emoji) VALUES
(1, '人参', 'Panax ginseng', '山地针阔混交林', '根及根茎', '大补元气，复脉固脱', '近危', 5, '🌱'),
(2, '薄荷', 'Mentha haplocalyx', '沟边路旁潮湿地', '地上部分', '疏散风热，清利头目', '无危', 4, '🍃'),
(3, '金银花', 'Lonicera japonica', '向阳山坡灌丛', '花蕾', '清热解毒，凉散风热', '无危', 5, '⚪');

-- 4) knowledge_resource：知识资料/电子读物（正文、浏览量等）
INSERT INTO knowledge_resource (title, author, cover_emoji, pages, description, content, views, created_at) VALUES
('本草纲目（节选）', '李时珍', '📗', 120, '经典本草文献节选', '内容为示例：记录多种药用植物的性味与主治，便于检索与学习。', 42, UNIX_TIMESTAMP(NOW()) * 1000),
('药用植物栽培基础', '示例编者', '📘', 86, '栽培环境与田间管理入门', '示例章节：选地、整地、繁殖方式与病虫害防治要点。', 18, UNIX_TIMESTAMP(NOW()) * 1000);

-- 5) medicinal_chat_history：智能问答对话记录（user_id 对应 sys_user.id；role 如 user/assistant）
INSERT INTO medicinal_chat_history (user_id, session_id, role, content, tokens_used, created_at) VALUES
(1, 'sess-demo-001', 'user', '人参有什么功效？', 12, NOW()),
(1, 'sess-demo-001', 'assistant', '人参具有大补元气、复脉固脱等功效（示例回复，非真实问诊）。', 48, NOW()),
(2, 'sess-demo-002', 'user', '薄荷叶可以怎么用？', 10, NOW()),
(2, 'sess-demo-002', 'assistant', '常见用法包括泡茶、配伍入方等，具体需遵医嘱（示例）。', 36, NOW());
