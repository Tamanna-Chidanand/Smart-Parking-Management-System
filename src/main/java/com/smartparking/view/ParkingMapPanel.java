package com.smartparking.view;

import com.smartparking.controller.ParkingSpotController;
import com.smartparking.model.ParkingSpot;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ParkingMapPanel extends JPanel {
    private final ParkingSpotController spotController;
    private final Map<String, Color> statusColors;
    private final Map<String, Color> typeColors;
    private final int SPOT_SIZE = 80;
    private final int SPOTS_PER_ROW = 5;
    private final int PADDING = 10;
    
    public ParkingMapPanel(ParkingSpotController spotController) {
        this.spotController = spotController;
        
        // Initialize status colors
        statusColors = new HashMap<>();
        statusColors.put("AVAILABLE", new Color(76, 175, 80));  // Green
        statusColors.put("OCCUPIED", new Color(244, 67, 54));   // Red
        
        // Initialize type colors
        typeColors = new HashMap<>();
        typeColors.put("STANDARD", new Color(33, 150, 243));    // Blue
        typeColors.put("HANDICAPPED", new Color(156, 39, 176)); // Purple
        typeColors.put("ELECTRIC", new Color(255, 193, 7));     // Amber
        
        // Force a 4x4 grid for a perfect square
        int gridSize = 4;
        int mapSize = gridSize * (SPOT_SIZE + PADDING) + PADDING;
        
        // Set preferred size to make it a perfect square with space for legend
        setPreferredSize(new Dimension(
            mapSize + 200, // Extra space for legend
            mapSize + 60   // Extra space for button
        ));
        
        // Add refresh button
        JButton refreshButton = new JButton("Refresh Map");
        refreshButton.addActionListener(e -> repaint());
        
        setLayout(new BorderLayout());
        add(refreshButton, BorderLayout.NORTH);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        List<ParkingSpot> spots = spotController.getAllSpots();
        int x = PADDING;
        int y = PADDING + 40; // Extra space for the refresh button
        
        // Force a 4x4 grid
        int gridSize = 4;
        
        // Draw grid lines
        g2d.setColor(new Color(200, 200, 200));
        g2d.setStroke(new BasicStroke(1));
        
        // Draw horizontal lines
        for (int i = 0; i <= gridSize; i++) {
            int lineY = y + i * (SPOT_SIZE + PADDING);
            g2d.drawLine(PADDING, lineY, PADDING + gridSize * (SPOT_SIZE + PADDING), lineY);
        }
        
        // Draw vertical lines
        for (int i = 0; i <= gridSize; i++) {
            int lineX = x + i * (SPOT_SIZE + PADDING);
            g2d.drawLine(lineX, y, lineX, y + gridSize * (SPOT_SIZE + PADDING));
        }
        
        // Draw parking spots
        for (ParkingSpot spot : spots) {
            // Calculate position based on spot number
            int row = spot.getSpotNumber().charAt(0) - 'A';
            int col = Integer.parseInt(spot.getSpotNumber().substring(1)) - 1;
            
            if (row >= 0 && row < gridSize && col >= 0 && col < gridSize) {
                int spotX = x + col * (SPOT_SIZE + PADDING);
                int spotY = y + row * (SPOT_SIZE + PADDING);
                
                // Determine status based on isAvailable()
                String status = spot.isAvailable() ? "AVAILABLE" : "OCCUPIED";
                
                // Draw spot rectangle with type color as border
                g2d.setColor(statusColors.getOrDefault(status, Color.GRAY));
                g2d.fillRoundRect(spotX, spotY, SPOT_SIZE, SPOT_SIZE, 10, 10);
                
                // Draw type color as border
                String spotType = spot.getType().toUpperCase();
                g2d.setColor(typeColors.getOrDefault(spotType, Color.BLACK));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(spotX, spotY, SPOT_SIZE, SPOT_SIZE, 10, 10);
                
                // Draw spot number
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                String spotNumber = spot.getSpotNumber();
                FontMetrics metrics = g2d.getFontMetrics();
                int textX = spotX + (SPOT_SIZE - metrics.stringWidth(spotNumber)) / 2;
                int textY = spotY + (SPOT_SIZE - metrics.getHeight()) / 2 + metrics.getAscent();
                g2d.drawString(spotNumber, textX, textY);
                
                // Draw spot type
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                textY = textY + 20;
                textX = spotX + (SPOT_SIZE - metrics.stringWidth(spotType)) / 2;
                g2d.drawString(spotType, textX, textY);
            }
        }
        
        // Draw legends
        drawStatusLegend(g2d);
        drawTypeLegend(g2d);
    }
    
    private void drawStatusLegend(Graphics2D g2d) {
        int legendX = getWidth() - 180;
        int legendY = 50;
        int boxSize = 20;
        int spacing = 30;
        
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Status:", legendX, legendY - 5);
        
        for (Map.Entry<String, Color> entry : statusColors.entrySet()) {
            g2d.setColor(entry.getValue());
            g2d.fillRect(legendX, legendY, boxSize, boxSize);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(legendX, legendY, boxSize, boxSize);
            
            g2d.setColor(Color.BLACK);
            g2d.drawString(entry.getKey(), legendX + boxSize + 5, legendY + 15);
            
            legendY += spacing;
        }
    }
    
    private void drawTypeLegend(Graphics2D g2d) {
        int legendX = getWidth() - 180;
        int legendY = 150;
        int boxSize = 20;
        int spacing = 30;
        
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Type:", legendX, legendY - 5);
        
        for (Map.Entry<String, Color> entry : typeColors.entrySet()) {
            g2d.setColor(entry.getValue());
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(legendX, legendY, boxSize, boxSize);
            
            g2d.setColor(Color.BLACK);
            g2d.drawString(entry.getKey(), legendX + boxSize + 5, legendY + 15);
            
            legendY += spacing;
        }
    }
} 