-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 29-01-2026 a las 18:15:47
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `huella_carbono_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `actividad`
--

CREATE TABLE `actividad` (
  `id_actividad` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `id_categoria` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `actividad`
--

INSERT INTO `actividad` (`id_actividad`, `nombre`, `id_categoria`) VALUES
(1, 'Conducir coche', 1),
(2, 'Usar transporte público', 1),
(3, 'Viajar en avión', 1),
(4, 'Consumo eléctrico', 2),
(5, 'Consumo de gas', 2),
(6, 'Comer carne de res', 3),
(7, 'Comer alimentos vegetarianos', 3),
(8, 'Generar residuos domésticos', 4),
(9, 'Consumo de agua potable', 5);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria`
--

CREATE TABLE `categoria` (
  `id_categoria` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `factor_emision` float NOT NULL,
  `unidad` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `categoria`
--

INSERT INTO `categoria` (`id_categoria`, `nombre`, `factor_emision`, `unidad`) VALUES
(1, 'Transporte', 0.21, 'km'),
(2, 'Energía', 0.233, 'kWh'),
(3, 'Alimentación', 2.5, 'kg'),
(4, 'Residuos', 0.41, 'kg'),
(5, 'Agua', 0.35, 'm3');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `habito`
--

CREATE TABLE `habito` (
  `id_usuario` int(11) NOT NULL,
  `id_actividad` int(11) NOT NULL,
  `frecuencia` int(11) DEFAULT NULL,
  `tipo` tinytext DEFAULT NULL,
  `ultima_fecha` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `habito`
--

INSERT INTO `habito` (`id_usuario`, `id_actividad`, `frecuencia`, `tipo`, `ultima_fecha`) VALUES
(1, 1, 3, 'semanal', '2025-01-21'),
(1, 2, 5, 'semanal', '2025-01-22'),
(2, 4, 1, 'diaria', '2025-02-01'),
(2, 6, 2, 'semanal', '2025-02-07'),
(3, 1, 3, 'Semanal', '2026-01-19'),
(3, 2, 2, 'Semanal', '2026-01-19'),
(3, 6, 1, 'Mensual', '2026-01-19'),
(4, 2, 5, 'Semanal', '2026-01-27'),
(4, 4, 1, 'Semanal', '2026-01-27'),
(4, 5, 2, 'Semanal', '2026-01-27');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `huella`
--

CREATE TABLE `huella` (
  `id_registro` int(11) NOT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  `id_actividad` int(11) DEFAULT NULL,
  `valor` float NOT NULL,
  `unidad` varchar(50) NOT NULL,
  `fecha` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `huella`
--

INSERT INTO `huella` (`id_registro`, `id_usuario`, `id_actividad`, `valor`, `unidad`, `fecha`) VALUES
(1, 3, 1, 150, 'km', '2025-01-20'),
(2, 1, 4, 30, 'kWh', '2025-01-15'),
(3, 2, 6, 1, 'kg', '2025-02-05'),
(4, 2, 8, 5, 'kg', '2025-02-10'),
(5, 1, 1, 150, 'km', '2026-01-14'),
(6, 1, 1, 150, 'km', '2026-01-14'),
(8, 3, 5, 500, 'kWh', '2026-01-06'),
(10, 3, 1, 30, 'km', '2026-01-16'),
(11, 3, 1, 150.5, 'km', '2025-12-15'),
(12, 1, 2, 80, 'kWh', '2026-01-10'),
(13, 1, 3, 20.2, 'kg', '2026-01-18'),
(14, 3, 7, 3, 'kg', '2026-01-20'),
(15, 4, 4, 40, 'kWh', '2026-01-27'),
(16, 4, 1, 23, 'km', '2026-01-27'),
(17, 4, 5, 11, 'm³', '2025-12-24'),
(18, 4, 7, 4, 'kg', '2026-01-27');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `recomendacion`
--

CREATE TABLE `recomendacion` (
  `id_recomendacion` int(11) NOT NULL,
  `id_categoria` int(11) DEFAULT NULL,
  `descripcion` tinytext NOT NULL,
  `impacto_estimado` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `recomendacion`
--

INSERT INTO `recomendacion` (`id_recomendacion`, `id_categoria`, `descripcion`, `impacto_estimado`) VALUES
(1, 1, 'Usa bicicleta o camina en distancias cortas', 30),
(2, 1, 'Opta por el transporte público en vez del coche', 45),
(3, 1, 'Compartir coche con compañeros reduce emisiones', 20),
(4, 2, 'Apaga dispositivos eléctricos cuando no los uses', 10),
(5, 2, 'Usa bombillas LED en lugar de incandescentes', 15),
(6, 3, 'Reduce el consumo de carne de res y opta por vegetales', 50),
(7, 3, 'Compra productos locales y de temporada', 20),
(8, 4, 'Recicla residuos para disminuir emisiones', 25),
(9, 4, 'Reduce el uso de plásticos desechables', 10),
(10, 5, 'Reduce el tiempo de ducha y ahorra agua', 5);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `fecha_registro` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `nombre`, `email`, `contrasena`, `fecha_registro`) VALUES
(1, 'Usuario Ejemplo 1', 'user1@example.com', 'pass123', '2025-01-01'),
(2, 'Usuario Ejemplo 2', 'user2@example.com', 'pass456', '2025-01-10'),
(3, 'David Montoro Guillen', 'david@gmail.com', 'Usuario_1', '2026-01-14'),
(4, 'Juan', 'juan@gmail.com', '$2a$10$x.Q3r7MaXJyDMZRZVz4lNekJndMpa2cHNTghtK8491gIyjLI/BrXW', '2026-01-27');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `actividad`
--
ALTER TABLE `actividad`
  ADD PRIMARY KEY (`id_actividad`),
  ADD KEY `id_categoria` (`id_categoria`);

--
-- Indices de la tabla `categoria`
--
ALTER TABLE `categoria`
  ADD PRIMARY KEY (`id_categoria`);

--
-- Indices de la tabla `habito`
--
ALTER TABLE `habito`
  ADD PRIMARY KEY (`id_usuario`,`id_actividad`),
  ADD KEY `id_actividad` (`id_actividad`);

--
-- Indices de la tabla `huella`
--
ALTER TABLE `huella`
  ADD PRIMARY KEY (`id_registro`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `id_actividad` (`id_actividad`);

--
-- Indices de la tabla `recomendacion`
--
ALTER TABLE `recomendacion`
  ADD PRIMARY KEY (`id_recomendacion`),
  ADD KEY `id_categoria` (`id_categoria`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `actividad`
--
ALTER TABLE `actividad`
  MODIFY `id_actividad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `categoria`
--
ALTER TABLE `categoria`
  MODIFY `id_categoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `huella`
--
ALTER TABLE `huella`
  MODIFY `id_registro` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `recomendacion`
--
ALTER TABLE `recomendacion`
  MODIFY `id_recomendacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `actividad`
--
ALTER TABLE `actividad`
  ADD CONSTRAINT `actividad_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`);

--
-- Filtros para la tabla `habito`
--
ALTER TABLE `habito`
  ADD CONSTRAINT `habito_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  ADD CONSTRAINT `habito_ibfk_2` FOREIGN KEY (`id_actividad`) REFERENCES `actividad` (`id_actividad`);

--
-- Filtros para la tabla `huella`
--
ALTER TABLE `huella`
  ADD CONSTRAINT `huella_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  ADD CONSTRAINT `huella_ibfk_2` FOREIGN KEY (`id_actividad`) REFERENCES `actividad` (`id_actividad`);

--
-- Filtros para la tabla `recomendacion`
--
ALTER TABLE `recomendacion`
  ADD CONSTRAINT `recomendacion_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
