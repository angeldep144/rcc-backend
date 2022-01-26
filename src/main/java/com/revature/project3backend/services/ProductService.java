package com.revature.project3backend.services;

import com.revature.project3backend.exceptions.InvalidValueException;
import com.revature.project3backend.models.Product;
import com.revature.project3backend.repositories.ProductRepo;
import com.revature.project3backend.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductService {
	private final ProductRepo productRepo;
	
	private final int postsPerPage = 20;
	
	@Autowired
	public ProductService (ProductRepo productRepo) {
		this.productRepo = productRepo;
	}
	
	public List <Product> getProducts (String searchQuery, Integer page) {
		return this.productRepo.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContaining (searchQuery, searchQuery, PageRequest.of (page, postsPerPage, Sort.by ("name"))).getContent ();
	}
	
	public Product getProduct (Integer id) throws InvalidValueException {
		Product product = this.productRepo.findById (id).orElse (null);
		
		if (product == null) {
			throw new InvalidValueException ("Invalid product id");
		}
		
		return product;
	}
	
	public void reduceStock (Product product, Integer quantity) throws InvalidValueException {
		int newStock = product.getStock () - quantity;
		
		if (newStock < 0) {
			throw new InvalidValueException ("Invalid quantity");
		}
		
		product.setStock (newStock);
		
		productRepo.save (product);
	}
	
	/**
	 * Updates the product information in the database
	 *
	 * @param product The new product information.
	 * @param file    The new image for the product if desired.
	 * @return The updated product.
	 */
	public Product updateProduct (Product product, MultipartFile file) throws InvalidValueException {
		if(product.getName() == null){
			throw new InvalidValueException("No product name");
		}

		if(product.getDescription() == null){
			throw new InvalidValueException("No product description");
		}

		if (file != null) {
			product.setImageUrl (FileUtil.uploadToS3 (product, file));
		}
		
		Product updatedProduct = this.productRepo.save (product);
		
		return updatedProduct;
	}
	
	/**
	 * Method creates new product given new product information
	 *
	 * @param product created from form data received from controller
	 * @return newly created product
	 * @throws InvalidValueException when price or sales price is less than 0, or sales price is greater than original price
	 */
	public Product createProduct (Product product, MultipartFile file) throws InvalidValueException {
		if(product.getName() == null){
			throw new InvalidValueException("No product name");
		}

		if(product.getDescription() == null){
			throw new InvalidValueException("No product description");
		}

		if (file != null) {
			product.setImageUrl (FileUtil.uploadToS3 (product, file));
		}

		if(product.getSalePrice() != null){
			if (product.getSalePrice () < 0) {
				throw new InvalidValueException ("Sale price cannot be less than 0");
			}
			if (product.getSalePrice () > product.getPrice ()) {
				throw new InvalidValueException ("Sales price cannot be greater than original price");
			}
		}

		if (product.getPrice () < 0){
			throw new InvalidValueException ("Price cannot be less than 0");
		}

		
		Product product2 = this.productRepo.save (product);
		
		return product2;
	}
}
