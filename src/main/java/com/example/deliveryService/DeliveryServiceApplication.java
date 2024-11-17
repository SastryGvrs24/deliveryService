package com.example.deliveryService;

import com.example.deliveryService.domain.*;
import com.example.deliveryService.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DeliveryServiceApplication implements CommandLineRunner {

    @Autowired
    DeliveryPersonnelRepository deliveryPersonnelRepository;

    @Autowired
    private RoleRepository rolesRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantOwnerRepository restaurantOwnerRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        // initial role - data
        for(RoleEnum role : RoleEnum.values()) {
            rolesRepository.save(new Role(role.toString()));
        }

        DeliveryPersonnel deliveryPersonnel = new DeliveryPersonnel("sastry", passwordEncoder.encode("sastry"), "Sastry Gvrs", "9951435620","Two Wheeler", true);
        deliveryPersonnel.setRoles(List.of(rolesRepository.findByRoleName(RoleEnum.ROLE_DELIVERY_PERSONNEL.toString())));
        deliveryPersonnelRepository.save(deliveryPersonnel);

        Customer customer = new Customer("achyut", passwordEncoder.encode("achyut"), "achyut", "Hyderabad", "credit card");
        customer.setRoles(List.of(rolesRepository.findByRoleName(RoleEnum.ROLE_CUSTOMER.toString())));
        customerRepository.save(customer);

        initRestaurantOwners();

        initRestaurantMenuItems();


    }

    void initRestaurantOwners() {
        List<RestaurantOwner> restaurantOwners = new ArrayList<>();

        RestaurantOwner owner1 = new RestaurantOwner(
                "nikshay",
                passwordEncoder.encode("nikshay"),
                "Sai Nikshay",
                "Nikshay Restaurant",
                "Banjara Hills, Hyderabad",
                "6AM - 12AM"
        );
        restaurantOwners.add(owner1);

        RestaurantOwner owner2 = new RestaurantOwner(
                "anisha",
                passwordEncoder.encode("anisha"),
                "Anisha Rao",
                "Anisha's Café",
                "Jubilee Hills, Hyderabad",
                "8AM - 11PM"
        );
        restaurantOwners.add(owner2);

        RestaurantOwner owner3 = new RestaurantOwner(
                "rahul",
                passwordEncoder.encode("rahul"),
                "Rahul Sharma",
                "Rahul's Grill House",
                "Madhapur, Hyderabad",
                "10AM - 10PM"
        );
        restaurantOwners.add(owner3);

        RestaurantOwner owner4 = new RestaurantOwner(
                "priya",
                passwordEncoder.encode("priya"),
                "Priya Singh",
                "Priya's Desserts",
                "Gachibowli, Hyderabad",
                "12PM - 12AM"
        );
        restaurantOwners.add(owner4);

        RestaurantOwner owner5 = new RestaurantOwner(
                "vishal",
                passwordEncoder.encode("vishal"),
                "Vishal Kumar",
                "Vishal's Diner",
                "Kondapur, Hyderabad",
                "7AM - 11PM"
        );
        restaurantOwners.add(owner5);

        restaurantOwnerRepository.saveAll(restaurantOwners);
    }


    void initRestaurantMenuItems() {

// Generate Menu Items for Each Restaurant Owner
        List<MenuItem> menuItems = new ArrayList<>();

// Fetch restaurant owners from the repository
        RestaurantOwner owner1 = restaurantOwnerRepository.findByUsername("nikshay");
        RestaurantOwner owner2 = restaurantOwnerRepository.findByUsername("anisha");
        RestaurantOwner owner3 = restaurantOwnerRepository.findByUsername("rahul");
        RestaurantOwner owner4 = restaurantOwnerRepository.findByUsername("priya");
        RestaurantOwner owner5 = restaurantOwnerRepository.findByUsername("vishal");

// Menu for Nikshay Restaurant
        menuItems.add(new MenuItem(
                "Hyderabadi Dum Biryani",
                "Traditional biryani cooked with fragrant spices and tender chicken.",
                280.00,
                true,
                owner1,
                "Indian"
        ));
        menuItems.add(new MenuItem(
                "Mutton Rogan Josh",
                "Aromatic mutton curry with a blend of Kashmiri spices.",
                350.00,
                true,
                owner1,
                "Indian"
        ));

// Menu for Anisha's Café
        menuItems.add(new MenuItem(
                "Cappuccino",
                "Hot coffee topped with foamy milk.",
                150.00,
                true,
                owner2,
                "Beverage"
        ));
        menuItems.add(new MenuItem(
                "Grilled Veg Sandwich",
                "Toasted sandwich with fresh vegetables and cheese.",
                120.00,
                true,
                owner2,
                "Cafe"
        ));

// Menu for Rahul's Grill House
        menuItems.add(new MenuItem(
                "BBQ Chicken Wings",
                "Juicy chicken wings grilled with tangy BBQ sauce.",
                200.00,
                true,
                owner3,
                "Grill"
        ));
        menuItems.add(new MenuItem(
                "Lamb Chops",
                "Perfectly grilled lamb chops with herbs and spices.",
                400.00,
                true,
                owner3,
                "Grill"
        ));

// Menu for Priya's Desserts
        menuItems.add(new MenuItem(
                "Red Velvet Cake",
                "Moist red velvet cake with cream cheese frosting.",
                180.00,
                true,
                owner4,
                "Dessert"
        ));
        menuItems.add(new MenuItem(
                "Mango Cheesecake",
                "Rich cheesecake with a mango twist.",
                200.00,
                true,
                owner4,
                "Dessert"
        ));

// Menu for Vishal's Diner
        menuItems.add(new MenuItem(
                "Cheeseburger",
                "Classic burger with cheese, lettuce, and tomato.",
                150.00,
                true,
                owner5,
                "Fast Food"
        ));
        menuItems.add(new MenuItem(
                "French Fries",
                "Golden and crispy french fries.",
                80.00,
                true,
                owner5,
                "Fast Food"
        ));

        menuItemRepository.saveAll(menuItems);
    }
}
