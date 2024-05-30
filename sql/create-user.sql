-- Drop user first if they exist
DROP USER if exists 'hikuser'@'%' ;

-- Now create user with prop privileges
CREATE USER 'hikuser'@'%' IDENTIFIED BY 'hiksecret';

GRANT ALL PRIVILEGES ON * . * TO 'hikuser'@'%';