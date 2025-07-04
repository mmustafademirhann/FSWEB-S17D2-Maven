package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.*;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;
    private final Taxable taxable;

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public ResponseEntity<List<Developer>> getAll() {
        return ResponseEntity.ok(new ArrayList<>(developers.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Developer> getById(@PathVariable int id) {
        Developer dev = developers.get(id);
        if (dev == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dev);
    }

    @PostMapping
    public ResponseEntity<Developer> create(@RequestBody Developer developer) {
        double salary = developer.getSalary();
        Developer newDev = null;

        switch (developer.getExperience()) {
            case JUNIOR -> newDev = new JuniorDeveloper(developer.getId(), developer.getName(),
                    salary - (salary * taxable.getSimpleTaxRate() / 100));
            case MID -> newDev = new MidDeveloper(developer.getId(), developer.getName(),
                    salary - (salary * taxable.getMiddleTaxRate() / 100));
            case SENIOR -> newDev = new SeniorDeveloper(developer.getId(), developer.getName(),
                    salary - (salary * taxable.getUpperTaxRate() / 100));
        }

        if (newDev != null) {
            developers.put(newDev.getId(), newDev);
            return new ResponseEntity<>(newDev, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Developer> update(@PathVariable int id, @RequestBody Developer developer) {
        developers.put(id, developer);
        return ResponseEntity.ok(developer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Developer> delete(@PathVariable int id) {
        Developer removed = developers.remove(id);
        if (removed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(removed);
    }
}
