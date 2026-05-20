package main.java.com.example.inventory.product;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    
    private Long id;

    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double price;

    private int quantity;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false) // Foreign key to the Category entity
    private Category category;
}